package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudoCommand extends BaseCommand {
    
    public SudoCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.sudo", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Check if we have enough arguments
        if (args.length < 2) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /sudo <player> <command/message>");
            return true;
        }
        
        // Get target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // Check if sender is trying to sudo a player with higher permissions
        if (sender instanceof Player && target.hasPermission("frizzlenessentials.sudo.exempt")) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.no-permission", 
                    "You cannot use sudo on this player.");
            return true;
        }
        
        // Build the command/message
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) builder.append(" ");
            builder.append(args[i]);
        }
        String action = builder.toString();
        
        // Check if it's a chat message or command
        if (action.startsWith("/")) {
            // Remove the leading slash and execute the command
            target.performCommand(action.substring(1));
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.sudo.command", 
                    "Made {player} execute: {command}", 
                    "player", target.getName(),
                    "command", action);
        } else {
            // Send chat message
            target.chat(action);
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.sudo.chat", 
                    "Made {player} say: {message}", 
                    "player", target.getName(),
                    "message", action);
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 