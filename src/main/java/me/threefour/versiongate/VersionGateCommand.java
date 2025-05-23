package me.threefour.versiongate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionGateCommand implements CommandExecutor, TabCompleter {
    private final VersionGate plugin;
    private final List<String> subCommands = Arrays.asList("reload", "info");

    public VersionGateCommand(VersionGate plugin) {
        this.plugin = plugin;
        registerSelf();
    }

    private void registerSelf() {
        try {
            // Create PluginCommand via reflection
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance("versiongate", plugin);

            command.setExecutor(this);
            command.setTabCompleter(this);
            command.setDescription("VersionGate main command");
            command.setUsage("/versiongate <reload|info>");
            command.setPermission("versiongate.use");

            // Register with command map
            CommandMap commandMap;
            try {
                commandMap = (CommandMap) Bukkit.getServer().getClass().getMethod("getCommandMap").invoke(Bukkit.getServer());
            } catch (NoSuchMethodException e) {
                // fallback for older servers
                java.lang.reflect.Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            }
            commandMap.register(plugin.getDescription().getName(), command);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not register command /versiongate: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                if (!sender.hasPermission("versiongate.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "VersionGate configuration reloaded successfully.");
                return true;

            case "info":
                if (!sender.hasPermission("versiongate.info")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                showInfo(sender);
                return true;

            default:
                showHelp(sender);
                return true;
        }
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== VersionGate Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/versiongate reload " + ChatColor.WHITE + "- Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/versiongate info " + ChatColor.WHITE + "- Show plugin information");
    }

    private void showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== VersionGate Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + String.join(", ", plugin.getDescription().getAuthors()));

        boolean viaVersionEnabled = plugin.getServer().getPluginManager().isPluginEnabled("ViaVersion");
        ChatColor viaVersionColor = viaVersionEnabled ? ChatColor.GREEN : ChatColor.RED;
        sender.sendMessage(ChatColor.YELLOW + "ViaVersion: " + viaVersionColor + viaVersionEnabled);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partialCommand = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partialCommand)) {
                    if (subCommand.equals("reload") && !sender.hasPermission("versiongate.reload")) continue;
                    if (subCommand.equals("info") && !sender.hasPermission("versiongate.info")) continue;
                    completions.add(subCommand);
                }
            }
        }
        return completions;
    }
}
