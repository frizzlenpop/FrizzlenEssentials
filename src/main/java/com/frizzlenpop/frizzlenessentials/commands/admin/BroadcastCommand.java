package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BroadcastCommand extends BaseCommand {
    
    public BroadcastCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.broadcast", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /broadcast <message>");
            return true;
        }
        
        // Build the message
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) builder.append(" ");
            builder.append(args[i]);
        }
        String message = builder.toString();
        
        // Get prefix from config
        String prefix = plugin.getConfigManager().getMainConfig().getString("broadcast.prefix", "§c[Broadcast]§r");
        
        // Format the message
        String formattedMessage = prefix + " " + message.replace("&", "§");
        
        // Broadcast the message
        Bukkit.broadcastMessage(formattedMessage);
        
        // Log the broadcast
        plugin.getLogger().info("Broadcast by " + sender.getName() + ": " + message);
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for broadcast command
        return new ArrayList<>();
    }
} 