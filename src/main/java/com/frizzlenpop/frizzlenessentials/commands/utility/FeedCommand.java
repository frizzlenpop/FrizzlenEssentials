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

public class FeedCommand extends BaseCommand {
    
    public FeedCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.feed", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        
        // Check if a target player is specified
        if (args.length > 0 && sender.hasPermission("frizzlenessentials.feed.others")) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        } else {
            player = getPlayer(sender);
        }
        
        // Feed the player
        player.setFoodLevel(20); // Max food level
        player.setSaturation(20f); // Max saturation
        
        // Send messages
        if (sender == player) {
            MessageUtils.sendConfigMessage(player, "messages.feed.fed", 
                    "Your hunger has been satisfied.");
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.feed.fed-other", 
                    "You have fed {player}.", 
                    "player", player.getName());
            
            MessageUtils.sendConfigMessage(player, "messages.feed.fed-by-other", 
                    "Your hunger has been satisfied by {player}.", 
                    "player", sender.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.feed.others")) {
            String partialName = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 