package com.frizzlenpop.frizzlenessentials.commands.utility;

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

public class SpeedCommand extends BaseCommand {
    
    public SpeedCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.speed", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        float speed;
        boolean flying;
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /speed <speed> [player] [fly|walk]");
            return true;
        }
        
        // Parse the speed value
        try {
            speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                MessageUtils.sendConfigMessage(sender, "messages.speed.invalid-speed", 
                        "Speed must be between 0 and 10.");
                return true;
            }
            // Convert to Minecraft speed (0-1 scale)
            speed = speed / 10;
        } catch (NumberFormatException e) {
            MessageUtils.sendConfigMessage(sender, "messages.speed.invalid-speed", 
                    "Invalid speed value.");
            return true;
        }
        
        // Check if a target player is specified
        if (args.length > 1 && sender.hasPermission("frizzlenessentials.speed.others")) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        } else {
            player = getPlayer(sender);
        }
        
        // Determine if we're setting flying or walking speed
        if (args.length > 2) {
            String type = args[2].toLowerCase();
            if (type.equals("fly")) {
                flying = true;
            } else if (type.equals("walk")) {
                flying = false;
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.speed.invalid-type", 
                        "Invalid speed type. Use 'fly' or 'walk'.");
                return true;
            }
        } else {
            // Default to flying speed if player is flying, walking speed otherwise
            flying = player.isFlying();
        }
        
        // Set the speed
        if (flying) {
            player.setFlySpeed(speed);
        } else {
            player.setWalkSpeed(speed);
        }
        
        // Send messages
        String type = flying ? "flying" : "walking";
        String displaySpeed = String.format("%.1f", speed * 10);
        
        if (sender == player) {
            MessageUtils.sendConfigMessage(player, "messages.speed.set", 
                    "Your {type} speed has been set to {speed}.", 
                    "type", type, 
                    "speed", displaySpeed);
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.speed.set-other", 
                    "Set {player}'s {type} speed to {speed}.", 
                    "player", player.getName(),
                    "type", type,
                    "speed", displaySpeed);
            
            MessageUtils.sendConfigMessage(player, "messages.speed.set-by-other", 
                    "Your {type} speed has been set to {speed} by {player}.", 
                    "type", type,
                    "speed", displaySpeed,
                    "player", sender.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            // Suggest some common speed values
            for (String speed : new String[]{"1", "2", "3", "4", "5", "10"}) {
                if (speed.startsWith(partial)) {
                    completions.add(speed);
                }
            }
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.speed.others")) {
            String partialName = args[1].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        } else if (args.length == 3) {
            String partial = args[2].toLowerCase();
            if ("fly".startsWith(partial)) completions.add("fly");
            if ("walk".startsWith(partial)) completions.add("walk");
        }
        
        return completions;
    }
} 