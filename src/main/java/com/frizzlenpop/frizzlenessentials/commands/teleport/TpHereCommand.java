package com.frizzlenpop.frizzlenessentials.commands.teleport;

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

public class TpHereCommand extends BaseCommand {
    
    public TpHereCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tphere", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /tphere <player>");
            return true;
        }
        
        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // Check if the player is trying to teleport themselves
        if (player.equals(target)) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-teleport-to-self", 
                    "You cannot teleport yourself to yourself.");
            return true;
        }
        
        // Teleport the target player to the sender
        boolean success = plugin.getTeleportManager().teleport(target, player.getLocation());
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.player-teleported-here", 
                    "Teleported {player} to your location.", 
                    "player", target.getName());
            
            MessageUtils.sendConfigMessage(target, "messages.teleport.teleported-by-admin", 
                    "You were teleported to {player}.", 
                    "player", player.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            String partialName = args[0].toLowerCase();
            
            // Only suggest players that are not the sender
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(player))
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 