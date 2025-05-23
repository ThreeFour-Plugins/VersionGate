package me.threefour.versiongate;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class VersionGate extends JavaPlugin {
    private static VersionGate instance;
    private Map<String, List<Integer>> worldBlockedVersions = new HashMap<>();
    private Map<String, List<Integer>> pluginBlockedVersions = new HashMap<>();
    private Map<Integer, List<String>> versionBlockedPlugins = new HashMap<>();
    private VersionGateListener listener;

    @Override
    public void onEnable() {
        instance = this;

        // Try to check for ViaVersion
        if (!checkViaVersion()) {
            getLogger().warning("ViaVersion not yet enabled. Waiting for PluginEnableEvent...");
            // Listen for ViaVersion being enabled, then proceed
            Listener waitForViaVersion = new Listener() {
                @EventHandler
                public void onPluginEnable(PluginEnableEvent event) {
                    if (event.getPlugin().getName().equalsIgnoreCase("ViaVersion")) {
                        if (checkViaVersion()) {
                            getLogger().info("ViaVersion is now enabled. Continuing VersionGate initialization...");
                            HandlerList.unregisterAll(this);
                            finishEnable();
                        }
                    }
                }
            };
            // Register anonymous listener
            getServer().getPluginManager().registerEvents(waitForViaVersion, this);
            return;
        }
        // Continue normally if ViaVersion is already enabled
        finishEnable();
    }

    // Actual initialization after ViaVersion is present
    private void finishEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        loadConfig();

        // Register event listener
        listener = new VersionGateListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        // Register command
        PluginCommand command = getCommand("versiongate");
        if (command != null) {
            VersionGateCommand commandExecutor = new VersionGateCommand(this);
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
        }

        // Log startup information
        logStartupInfo();

        getLogger().info("VersionGate has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VersionGate has been disabled.");
    }

    // Improved check: log and also try class lookup for debug
    private boolean checkViaVersion() {
        Plugin viaVersion = getServer().getPluginManager().getPlugin("ViaVersion");
        if (viaVersion == null) {
            // Try to detect class
            try {
                Class.forName("com.viaversion.viaversion.api.Via");
                getLogger().severe("ViaVersion plugin not found, but its classes are on the classpath. It may not be loaded yet.");
            } catch (ClassNotFoundException e) {
                getLogger().severe("ViaVersion plugin not found! Make sure ViaVersion.jar is in your plugins folder and loads BEFORE VersionGate.");
            }
            return false;
        } else if (!viaVersion.isEnabled()) {
            getLogger().severe("ViaVersion plugin found but it is NOT enabled! Waiting for it to enable.");
            return false;
        }
        getLogger().info("ViaVersion plugin detected and enabled.");
        return true;
    }

    public void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();

        // Clear existing maps
        worldBlockedVersions.clear();
        pluginBlockedVersions.clear();
        versionBlockedPlugins.clear();

        // Load world restrictions
        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String worldName : worldsSection.getKeys(false)) {
                List<Integer> blockedVersions = worldsSection.getIntegerList(worldName + ".blocked_versions");
                worldBlockedVersions.put(worldName, blockedVersions);
            }
        }

        // Load plugin interactions
        ConfigurationSection pluginInteractionsSection = config.getConfigurationSection("plugin_interactions");
        if (pluginInteractionsSection != null) {
            // Load plugin-based version blocks
            ConfigurationSection blockedByVersionSection = pluginInteractionsSection.getConfigurationSection("blocked_by_version");
            if (blockedByVersionSection != null) {
                for (String pluginName : blockedByVersionSection.getKeys(false)) {
                    List<Integer> versions = blockedByVersionSection.getIntegerList(pluginName);
                    pluginBlockedVersions.put(pluginName, versions);
                }
            }

            // Load version-based plugin blocks
            ConfigurationSection versionsSection = pluginInteractionsSection.getConfigurationSection("versions");
            if (versionsSection != null) {
                for (String versionStr : versionsSection.getKeys(false)) {
                    try {
                        int version = Integer.parseInt(versionStr);
                        List<String> plugins = versionsSection.getStringList(versionStr);
                        versionBlockedPlugins.put(version, plugins);
                    } catch (NumberFormatException e) {
                        getLogger().warning("Invalid version format in config: " + versionStr);
                    }
                }
            }
        }

        // Auto-detect plugins and add them to config if not already present
        autoDetectPlugins();
    }

    private void autoDetectPlugins() {
        FileConfiguration config = getConfig();
        ConfigurationSection pluginInteractionsSection = config.getConfigurationSection("plugin_interactions");
        if (pluginInteractionsSection == null) {
            pluginInteractionsSection = config.createSection("plugin_interactions");
        }

        ConfigurationSection blockedByVersionSection = pluginInteractionsSection.getConfigurationSection("blocked_by_version");
        if (blockedByVersionSection == null) {
            blockedByVersionSection = pluginInteractionsSection.createSection("blocked_by_version");
        }

        boolean configChanged = false;

        // Get all installed plugins
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String pluginName = plugin.getName();
            if (!pluginName.equals(this.getName()) && !blockedByVersionSection.contains(pluginName)) {
                // Add the plugin with an empty list if not already in config
                blockedByVersionSection.set(pluginName, new ArrayList<Integer>());
                configChanged = true;
            }
        }

        // Save config if changes were made
        if (configChanged) {
            saveConfig();
            getLogger().info("Added new plugins to config.yml. Please configure version restrictions as needed.");
        }
    }

    private void logStartupInfo() {
        getLogger().info("--------- VersionGate Startup Information ---------");
        getLogger().info("Supported protocol versions: All protocols supported by ViaVersion");
        getLogger().info("Configured worlds with restrictions: " + worldBlockedVersions.size());
        getLogger().info("Configured plugins with restrictions: " + pluginBlockedVersions.size());
        getLogger().info("Total version-specific plugin restrictions: " + versionBlockedPlugins.size());
        getLogger().info("---------------------------------------------------");
    }

    // Utility method to get player's protocol version
    public int getPlayerVersion(java.util.UUID playerUUID) {
        return Via.getAPI().getPlayerVersion(playerUUID);
    }

    // Check if a player is blocked from a world
    public boolean isPlayerBlockedFromWorld(int playerVersion, String worldName) {
        List<Integer> blockedVersions = worldBlockedVersions.get(worldName);
        return blockedVersions != null && blockedVersions.contains(playerVersion);
    }

    // Check if a plugin is blocked for a specific version
    public boolean isPluginBlockedForVersion(String pluginName, int playerVersion) {
        // Check plugin-specific restrictions
        List<Integer> blockedVersions = pluginBlockedVersions.get(pluginName);
        if (blockedVersions != null && blockedVersions.contains(playerVersion)) {
            return true;
        }

        // Check version-specific restrictions
        List<String> blockedPlugins = versionBlockedPlugins.get(playerVersion);
        return blockedPlugins != null && blockedPlugins.contains(pluginName);
    }

    // Get translated message with placeholders replaced
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getConfig().getString("messages." + key, "Message not found: " + key);
        message = ChatColor.translateAlternateColorCodes('&', message);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return message;
    }

    public static VersionGate getInstance() {
        return instance;
    }
}
