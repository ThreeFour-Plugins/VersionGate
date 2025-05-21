package me.threefour.versiongate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionGateCommand implements CommandExecutor, TabCompleter {
    private final VersionGate plugin;
    private final List<String> subCommands = Arrays.asList("reload", "info");
    
    public VersionGateCommand(VersionGate plugin) {
        this.plugin = plugin;
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
                    // Only add commands the sender has permission for
                    if (subCommand.equals("reload") && !sender.hasPermission("versiongate.reload")) {
                        continue;
                    }
                    if (subCommand.equals("info") && !sender.hasPermission("versiongate.info")) {
                        continue;
                    }
                    completions.add(subCommand);
                }
            }
        }
        
        return completions;
    }
} 