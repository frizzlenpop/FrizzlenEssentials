package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DebugCommand extends BaseCommand {
    
    public DebugCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.debug", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Toggle debug mode if no arguments
        if (args.length == 0) {
            boolean debug = !plugin.getConfigManager().getMainConfig().getBoolean("debug", false);
            plugin.getConfigManager().getMainConfig().set("debug", debug);
            plugin.getConfigManager().saveConfigs();
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.debug.toggled", 
                    "Debug mode {status}.", 
                    "status", debug ? "enabled" : "disabled");
            return true;
        }
        
        // Show debug information
        if (args[0].equalsIgnoreCase("info")) {
            // Server information
            sender.sendMessage("§6Server Information:");
            sender.sendMessage("§7- Version: §f" + Bukkit.getVersion());
            sender.sendMessage("§7- Bukkit Version: §f" + Bukkit.getBukkitVersion());
            sender.sendMessage("§7- Online Players: §f" + Bukkit.getOnlinePlayers().size());
            sender.sendMessage("§7- Max Players: §f" + Bukkit.getMaxPlayers());
            
            // Memory information
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            sender.sendMessage("§6Memory Information:");
            sender.sendMessage("§7- Max Memory: §f" + formatMemory(maxMemory));
            sender.sendMessage("§7- Total Memory: §f" + formatMemory(totalMemory));
            sender.sendMessage("§7- Used Memory: §f" + formatMemory(usedMemory));
            sender.sendMessage("§7- Free Memory: §f" + formatMemory(freeMemory));
            
            // Plugin information
            sender.sendMessage("§6Plugin Information:");
            sender.sendMessage("§7- Version: §f" + plugin.getDescription().getVersion());
            sender.sendMessage("§7- Debug Mode: §f" + plugin.getConfigManager().getMainConfig().getBoolean("debug", false));
            
            return true;
        }
        
        // Show usage if invalid argument
        MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                "Usage: /debug [info]");
        return true;
    }
    
    private String formatMemory(long bytes) {
        return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if ("info".startsWith(partial)) {
                completions.add("info");
            }
        }
        
        return completions;
    }
} 