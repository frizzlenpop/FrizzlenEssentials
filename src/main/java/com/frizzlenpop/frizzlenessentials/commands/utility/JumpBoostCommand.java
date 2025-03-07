package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JumpBoostCommand extends BaseCommand {
    
    public JumpBoostCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.jumpboost", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        int level;
        int duration;
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /jumpboost <level> [player] [duration]");
            return true;
        }
        
        // Parse the level value
        try {
            level = Integer.parseInt(args[0]);
            if (level < 0 || level > 10) {
                MessageUtils.sendConfigMessage(sender, "messages.jumpboost.invalid-level", 
                        "Level must be between 0 and 10.");
                return true;
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendConfigMessage(sender, "messages.jumpboost.invalid-level", 
                    "Invalid level value.");
            return true;
        }
        
        // Check if a target player is specified
        if (args.length > 1 && sender.hasPermission("frizzlenessentials.jumpboost.others")) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        } else {
            player = getPlayer(sender);
        }
        
        // Parse duration (in seconds), default to 60 seconds
        if (args.length > 2) {
            try {
                duration = Integer.parseInt(args[2]);
                if (duration < 1) {
                    MessageUtils.sendConfigMessage(sender, "messages.jumpboost.invalid-duration", 
                            "Duration must be greater than 0.");
                    return true;
                }
            } catch (NumberFormatException e) {
                MessageUtils.sendConfigMessage(sender, "messages.jumpboost.invalid-duration", 
                        "Invalid duration value.");
                return true;
            }
        } else {
            duration = 60;
        }
        
        // If level is 0, remove the effect
        if (level == 0) {
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
            
            if (sender == player) {
                MessageUtils.sendConfigMessage(player, "messages.jumpboost.removed", 
                        "Jump boost effect removed.");
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.jumpboost.removed-other", 
                        "Removed jump boost effect from {player}.", 
                        "player", player.getName());
                
                MessageUtils.sendConfigMessage(player, "messages.jumpboost.removed-by-other", 
                        "Your jump boost effect was removed by {player}.", 
                        "player", sender.getName());
            }
        } else {
            // Apply the jump boost effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration * 20, level - 1));
            
            if (sender == player) {
                MessageUtils.sendConfigMessage(player, "messages.jumpboost.applied", 
                        "Jump boost level {level} applied for {duration} seconds.", 
                        "level", String.valueOf(level),
                        "duration", String.valueOf(duration));
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.jumpboost.applied-other", 
                        "Applied jump boost level {level} to {player} for {duration} seconds.", 
                        "level", String.valueOf(level),
                        "player", player.getName(),
                        "duration", String.valueOf(duration));
                
                MessageUtils.sendConfigMessage(player, "messages.jumpboost.applied-by-other", 
                        "You received jump boost level {level} from {player} for {duration} seconds.", 
                        "level", String.valueOf(level),
                        "player", sender.getName(),
                        "duration", String.valueOf(duration));
            }
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            // Suggest some common levels
            for (String level : new String[]{"0", "1", "2", "3", "4", "5", "10"}) {
                if (level.startsWith(partial)) {
                    completions.add(level);
                }
            }
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.jumpboost.others")) {
            String partialName = args[1].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        } else if (args.length == 3) {
            String partial = args[2].toLowerCase();
            // Suggest some common durations
            for (String duration : new String[]{"30", "60", "300", "600", "3600"}) {
                if (duration.startsWith(partial)) {
                    completions.add(duration);
                }
            }
        }
        
        return completions;
    }
} 