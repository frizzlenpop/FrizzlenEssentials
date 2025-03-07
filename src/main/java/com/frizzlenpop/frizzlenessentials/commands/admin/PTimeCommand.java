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

public class PTimeCommand extends BaseCommand {
    
    public PTimeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.ptime", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /ptime <time|reset> [player]");
            return true;
        }
        
        // Get target player
        Player target = player;
        if (args.length > 1) {
            if (!player.hasPermission("frizzlenessentials.ptime.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to set time for other players.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Handle reset
        if (args[0].equalsIgnoreCase("reset")) {
            target.resetPlayerTime();
            
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.ptime.reset", 
                        "Your time has been reset to server time.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.ptime.reset-other", 
                        "Reset {player}'s time to server time.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.ptime.reset-by-other", 
                        "Your time was reset to server time by {player}.", 
                        "player", player.getName());
            }
            return true;
        }
        
        // Parse time
        long time;
        String timeArg = args[0].toLowerCase();
        
        try {
            switch (timeArg) {
                case "day":
                    time = 6000;
                    break;
                case "noon":
                    time = 6000;
                    break;
                case "sunset":
                    time = 12000;
                    break;
                case "night":
                    time = 18000;
                    break;
                case "midnight":
                    time = 18000;
                    break;
                case "sunrise":
                    time = 23000;
                    break;
                default:
                    // Try to parse as number
                    time = Long.parseLong(timeArg);
                    if (time < 0 || time > 24000) {
                        MessageUtils.sendConfigMessage(player, "messages.admin.ptime.invalid-time", 
                                "Time must be between 0 and 24000.");
                        return true;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendConfigMessage(player, "messages.admin.ptime.invalid-time", 
                    "Invalid time value. Use a number or day/night/sunrise/sunset.");
            return true;
        }
        
        // Set the player's time
        target.setPlayerTime(time, false);
        
        if (target == player) {
            MessageUtils.sendConfigMessage(player, "messages.admin.ptime.set", 
                    "Your time has been set to {time}.", 
                    "time", String.valueOf(time));
        } else {
            MessageUtils.sendConfigMessage(player, "messages.admin.ptime.set-other", 
                    "Set {player}'s time to {time}.", 
                    "player", target.getName(),
                    "time", String.valueOf(time));
            
            MessageUtils.sendConfigMessage(target, "messages.admin.ptime.set-by-other", 
                    "Your time was set to {time} by {player}.", 
                    "time", String.valueOf(time),
                    "player", player.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> times = List.of("day", "night", "noon", "midnight", "sunrise", "sunset", "reset");
            completions.addAll(times.stream()
                    .filter(time -> time.startsWith(partial))
                    .collect(Collectors.toList()));
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.ptime.others")) {
            String partial = args[1].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 