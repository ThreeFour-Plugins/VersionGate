package me.threefour.versiongate;

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

import java.util.*;

public class VersionGate extends JavaPlugin {
    private static VersionGate instance;
    private Map<String, List<Integer>> worldBlockedVersions = new HashMap<>();
    private Map<String, List<Integer>> pluginBlockedVersions = new HashMap<>();
    private Map<Integer, List<String>> versionBlockedPlugins = new HashMap<>();
    private VersionGateListener listener;

    @Override
    public void onEnable() {
        instance = this;

        if (!isViaVersionAvailable()) {
            getLogger().warning("ViaVersion not yet enabled. Waiting for PluginEnableEvent...");
            Listener viaListener = new Listener() {
                @EventHandler
                public void onPluginEnable(PluginEnableEvent event) {
                    if (event.getPlugin().getName().equalsIgnoreCase("ViaVersion")) {
                        if (isViaVersionAvailable()) {
                            getLogger().info("ViaVersion is now enabled. Continuing VersionGate initialization...");
                            HandlerList.unregisterAll(this);
                            finishEnable();
                        }
                    }
                }
            };
            getServer().getPluginManager().registerEvents(viaListener, this);
            return;
        }
        finishEnable();
    }

    private void finishEnable() {
        saveDefaultConfig();
        loadConfig();
        listener = new VersionGateListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        PluginCommand command = getCommand("versiongate");
        if (command != null) {
            VersionGateCommand commandExecutor = new VersionGateCommand(this);
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
        }
        logStartupInfo();
        getLogger().info("VersionGate has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VersionGate has been disabled.");
    }

    public boolean isViaVersionAvailable() {
        Plugin viaVersion = getServer().getPluginManager().getPlugin("ViaVersion");
        return viaVersion != null && viaVersion.isEnabled() && isViaClassLoaded();
    }

    private boolean isViaClassLoaded() {
        try {
            Class.forName("com.viaversion.viaversion.api.Via");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public int getPlayerVersion(UUID playerUUID) {
        if (!isViaVersionAvailable()) return -1;
        try {
            // Only reference ViaVersion API if present
            return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(playerUUID);
        } catch (Throwable t) {
            getLogger().warning("ViaVersion API error: " + t.getMessage());
            return -1;
        }
    }

    public void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();

        worldBlockedVersions.clear();
        pluginBlockedVersions.clear();
        versionBlockedPlugins.clear();

        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String worldName : worldsSection.getKeys(false)) {
                List<Integer> blockedVersions = worldsSection.getIntegerList(worldName + ".blocked_versions");
                worldBlockedVersions.put(worldName, blockedVersions);
            }
        }

        ConfigurationSection pluginInteractionsSection = config.getConfigurationSection("plugin_interactions");
        if (pluginInteractionsSection != null) {
            ConfigurationSection blockedByVersionSection = pluginInteractionsSection.getConfigurationSection("blocked_by_version");
            if (blockedByVersionSection != null) {
                for (String pluginName : blockedByVersionSection.getKeys(false)) {
                    List<Integer> versions = blockedByVersionSection.getIntegerList(pluginName);
                    pluginBlockedVersions.put(pluginName, versions);
                }
            }
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
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String pluginName = plugin.getName();
            if (!pluginName.equals(this.getName()) && !blockedByVersionSection.contains(pluginName)) {
                blockedByVersionSection.set(pluginName, new ArrayList<Integer>());
                configChanged = true;
            }
        }
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

    public boolean isPlayerBlockedFromWorld(int playerVersion, String worldName) {
        List<Integer> blockedVersions = worldBlockedVersions.get(worldName);
        return blockedVersions != null && blockedVersions.contains(playerVersion);
    }

    public boolean isPluginBlockedForVersion(String pluginName, int playerVersion) {
        List<Integer> blockedVersions = pluginBlockedVersions.get(pluginName);
        if (blockedVersions != null && blockedVersions.contains(playerVersion)) {
            return true;
        }
        List<String> blockedPlugins = versionBlockedPlugins.get(playerVersion);
        return blockedPlugins != null && blockedPlugins.contains(pluginName);
    }

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
