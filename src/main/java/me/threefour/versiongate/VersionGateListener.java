package me.threefour.versiongate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

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

    // Completely block login if the default world is blocked
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        // At login, player isn't in a world yet; get the default world
        String worldName = Bukkit.getWorlds().get(0).getName();
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return; // ViaVersion not available

        if (plugin.isPlayerBlockedFromWorld(playerVersion, worldName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", worldName);

            String kickMessage = plugin.getMessage("kick_on_login", placeholders);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);

            plugin.getLogger().info("Blocked login for player " + player.getName() + " (version: " + playerVersion + ") - world blocked: " + worldName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // No-op: all handling is done in teleport/login/respawn
    }

    // Block teleport/portal/respawn to blocked worlds
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (event.getTo() == null) return;
        String toWorldName = event.getTo().getWorld().getName();
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return;

        if (plugin.isPlayerBlockedFromWorld(playerVersion, toWorldName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("version", String.valueOf(playerVersion));
            placeholders.put("world", toWorldName);

            String cancelMessage = plugin.getMessage("block_world_access", placeholders);

            event.setCancelled(true);
            player.sendMessage(cancelMessage);

            plugin.getLogger().info("Blocked player " + player.getName() + " (version: " + playerVersion + ") from teleporting to blocked world " + toWorldName);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        // Logic handled by onPlayerTeleport
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String respawnWorld = event.getRespawnLocation().getWorld().getName();
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return;

        if (plugin.isPlayerBlockedFromWorld(playerVersion, respawnWorld)) {
            // Send to main world spawn if blocked
            World mainWorld = Bukkit.getWorlds().get(0);
            event.setRespawnLocation(mainWorld.getSpawnLocation());
            player.sendMessage(ChatColor.RED + "You cannot respawn in a blocked world, sent to spawn.");
        }
    }

    // Block all interaction in blocked worlds
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setKeepInventory(true); // prevent item loss in a blocked world
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    // Block plugin commands for blocked versions and in blocked worlds
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String command = event.getMessage().substring(1);
        String[] parts = command.split("\\s+");
        if (parts.length == 0) return;
        String baseCommand = parts[0].toLowerCase();
        int playerVersion = plugin.getPlayerVersion(playerUUID);
        if (playerVersion == -1) return;

        // Block all commands if in blocked world
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use commands in a blocked world.");
            return;
        }

        // Block version-blocked plugins anywhere
        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            if (!isCommandForPlugin(serverPlugin, baseCommand)) continue;
            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use commands from blocked plugin " + pluginName + " on your version.");
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

    // Block plugin messaging for blocked version plugins and in blocked worlds
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (player == null) return;
        int playerVersion = plugin.getPlayerVersion(player.getUniqueId());
        if (playerVersion == -1) return;
        if (plugin.isPlayerBlockedFromWorld(playerVersion, player.getWorld().getName())) {
            return; // ignore messages in blocked world
        }
        for (Plugin serverPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            String pluginName = serverPlugin.getName();
            if (!channel.toLowerCase().contains(pluginName.toLowerCase())) continue;
            if (plugin.isPluginBlockedForVersion(pluginName, playerVersion)) {
                player.sendMessage(ChatColor.RED + "Blocked plugin message from " + pluginName + " on your version.");
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
