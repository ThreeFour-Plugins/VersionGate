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
        
        // Register as a plugin message listener for common channels
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "minecraft:brand", this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String worldName = player.getWorld().getName();
        
        // Get player's protocol version from ViaVersion
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        
        // Check if player's version is blocked in the login world
        if (plugin.isPlayerBlockedFromWorld(playerVersion, worldName)) {
            // Create placeholders for the message
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", worldName);
            
            // Get configured kick message and replace placeholders
            String kickMessage = plugin.getMessage("kick_on_login", placeholders);
            
            // Kick the player
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
        
        // Skip processing if teleporting within the same world
        if (fromWorldName.equals(toWorldName)) return;
        
        // Get player's protocol version from ViaVersion
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        
        // Check if player's version is blocked in the destination world
        if (plugin.isPlayerBlockedFromWorld(playerVersion, toWorldName)) {
            // Create placeholders for the message
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", toWorldName);
            
            // Get configured teleport cancel message and replace placeholders
            String cancelMessage = plugin.getMessage("cancel_on_teleport", placeholders);
            
            // Cancel the teleport
            event.setCancelled(true);
            player.sendMessage(cancelMessage);
            
            plugin.getLogger().info("Blocked player " + player.getName() + " (version: " + playerVersion + ") from teleporting to world " + toWorldName);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        // This event extends PlayerTeleportEvent, so the actual logic is handled there
        // This handler is only here for clarity and potential future portal-specific logic
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String command = event.getMessage().substring(1); // Remove the leading slash
        String[] parts = command.split("\\s+");
        
        if (parts.length == 0) return;
        
        String baseCommand = parts[0].toLowerCase();
        
        // Get player's protocol version from ViaVersion
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        
        // Check if the command belongs to a plugin
        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            
            // Skip if it's not the command sender's plugin or command doesn't match
            if (!isCommandForPlugin(serverPlugin, baseCommand)) continue;
            
            // Check if the plugin is blocked for this player's version
            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                // Create placeholders for the message
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("version", String.valueOf(playerVersion));
                placeholders.put("plugin", pluginName);
                
                // Get configured command cancel message and replace placeholders
                String cancelMessage = plugin.getMessage("cancel_on_command", placeholders);
                
                // Cancel the command
                event.setCancelled(true);
                player.sendMessage(cancelMessage);
                
                plugin.getLogger().info("Blocked player " + player.getName() + " (version: " + playerVersion + ") from using command /" + baseCommand + " from plugin " + pluginName);
                return;
            }
        }
    }
    
    private boolean isCommandForPlugin(Plugin plugin, String command) {
        // Check main plugin command
        if (plugin.getName().equalsIgnoreCase(command)) return true;
        
        // Check command aliases from plugin.yml
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
        
        // Check which plugin the message belongs to
        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            
            // Simple check - if channel contains plugin name (not perfect but a reasonable heuristic)
            if (!channel.toLowerCase().contains(pluginName.toLowerCase())) continue;
            
            // Check if the plugin is blocked for this player's version
            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                // Create placeholders for the message
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("version", String.valueOf(playerVersion));
                placeholders.put("plugin", pluginName);
                
                // Get configured plugin message cancel message and replace placeholders
                String cancelMessage = plugin.getMessage("cancel_on_plugin_message", placeholders);
                
                // We can't cancel here, but we can notify the player
                player.sendMessage(cancelMessage);
                
                plugin.getLogger().info("Plugin message received on channel " + channel + " for player " + player.getName() + " (version: " + playerVersion + ") from plugin " + pluginName);
                return;
            }
        }
    }
    
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        // When a plugin is enabled, register for its channels if we need to monitor them
        Plugin enabledPlugin = event.getPlugin();
        if (!enabledPlugin.equals(plugin)) {
            plugin.getLogger().info("Plugin enabled: " + enabledPlugin.getName() + ". Monitoring for plugin message channels.");
        }
    }
} 