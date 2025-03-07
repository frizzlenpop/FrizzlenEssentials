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

public class TpaCommand extends BaseCommand {
    
    public TpaCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tpa", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /tpa <player>");
            return true;
        }
        
        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // Check if the player is trying to teleport to themselves
        if (player.equals(target)) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-teleport-to-self", 
                    "You cannot teleport to yourself.");
            return true;
        }
        
        // Check if there's already a pending request
        if (plugin.getTeleportManager().hasPendingRequest(player.getUniqueId())) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.already-pending", 
                    "You already have a pending teleport request.");
            return true;
        }
        
        // Send the teleport request
        boolean success = plugin.getTeleportManager().sendTeleportRequest(player, target, false);
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.request-sent", 
                    "Teleport request sent to {player}.", "player", target.getName());
            
            MessageUtils.sendConfigMessage(target, "messages.teleport.request-received", 
                    "{player} has requested to teleport to you. Type /tpaccept to accept or /tpdeny to deny.", 
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