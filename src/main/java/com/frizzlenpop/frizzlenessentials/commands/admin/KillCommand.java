package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KillCommand extends BaseCommand {
    
    public KillCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.kill", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /kill <player> [radius]");
            return true;
        }
        
        // Get target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // Check if target is exempt from kill
        if (target.hasPermission("frizzlenessentials.kill.exempt") && 
                !sender.hasPermission("frizzlenessentials.kill.bypass")) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.no-permission", 
                    "You cannot kill this player.");
            return true;
        }
        
        // Check if we have a radius argument
        if (args.length > 1) {
            if (!sender.hasPermission("frizzlenessentials.kill.entities")) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.no-permission", 
                        "You don't have permission to kill entities.");
                return true;
            }
            
            try {
                double radius = Double.parseDouble(args[1]);
                
                // Kill all entities within radius
                int killed = 0;
                for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
                    if (!(entity instanceof Player)) {
                        entity.remove();
                        killed++;
                    }
                }
                
                // Kill the target player
                target.setHealth(0);
                
                MessageUtils.sendConfigMessage(sender, "messages.admin.kill.radius", 
                        "Killed {player} and {count} entities within {radius} blocks.", 
                        "player", target.getName(),
                        "count", String.valueOf(killed),
                        "radius", String.valueOf(radius));
            } catch (NumberFormatException e) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-number", 
                        "Invalid radius value.");
                return true;
            }
        } else {
            // Just kill the target player
            target.setHealth(0);
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.kill.player", 
                    "Killed {player}.", 
                    "player", target.getName());
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