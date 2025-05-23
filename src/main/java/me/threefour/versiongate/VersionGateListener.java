package me.threefour.versiongate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VersionGateListener implements Listener, PluginMessageListener {
    private final VersionGate plugin;

    public VersionGateListener(VersionGate plugin) {
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "minecraft:brand", this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String worldName = player.getWorld().getName();

        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return; // ViaVersion not available

        if (plugin.isPlayerBlockedFromWorld(playerVersion, worldName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", worldName);

            String kickMessage = plugin.getMessage("kick_on_login", placeholders);

            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);
            plugin.getLogger().info("Kicked player " + player.getName() + " (version: " + playerVersion + ") from world " + worldName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) return;

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String fromWorldName = event.getFrom().getWorld().getName();
        String toWorldName = event.getTo().getWorld().getName();

        if (fromWorldName.equals(toWorldName)) return;

        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return; // ViaVersion not available

        if (plugin.isPlayerBlockedFromWorld(playerVersion, toWorldName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", toWorldName);

            String cancelMessage = plugin.getMessage("cancel_on_teleport", placeholders);

            event.setCancelled(true);
            player.sendMessage(cancelMessage);

            plugin.getLogger().info("Blocked player " + player.getName() + " (version: " + playerVersion + ") from teleporting to world " + toWorldName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        // This event extends PlayerTeleportEvent; logic is handled there.
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String command = event.getMessage().substring(1);
        String[] parts = command.split("\\s+");

        if (parts.length == 0) return;
        String baseCommand = parts[0].toLowerCase();

        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return; // ViaVersion not available

        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            if (!isCommandForPlugin(serverPlugin, baseCommand)) continue;

            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("version", String.valueOf(playerVersion));
                placeholders.put("plugin", pluginName);

                String cancelMessage = plugin.getMessage("cancel_on_command", placeholders);

                event.setCancelled(true);
                player.sendMessage(cancelMessage);

                plugin.getLogger().info("Blocked player " + player.getName() + " (version: " + playerVersion + ") from using command /" + baseCommand + " from plugin " + pluginName);
                return;
            }
        }
    }

    private boolean isCommandForPlugin(Plugin plugin, String command) {
        if (plugin.getName().equalsIgnoreCase(command)) return true;
        if (plugin.getDescription().getCommands() != null) {
            for (String cmd : plugin.getDescription().getCommands().keySet()) {
                if (cmd.equalsIgnoreCase(command)) return true;
            }
        }
        return false;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (player == null) return;

        UUID playerUUID = player.getUniqueId();
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return; // ViaVersion not available

        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            if (!channel.toLowerCase().contains(pluginName.toLowerCase())) continue;

            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("version", String.valueOf(playerVersion));
                placeholders.put("plugin", pluginName);

                String cancelMessage = plugin.getMessage("cancel_on_plugin_message", placeholders);
                player.sendMessage(cancelMessage);

                plugin.getLogger().info("Plugin message received on channel " + channel + " for player " + player.getName() + " (version: " + playerVersion + ") from plugin " + pluginName);
                return;
            }
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin enabledPlugin = event.getPlugin();
        if (!enabledPlugin.equals(plugin)) {
            plugin.getLogger().info("Plugin enabled: " + enabledPlugin.getName() + ". Monitoring for plugin message channels.");
        }
    }
}
