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

public class GodCommand extends BaseCommand {
    
    public GodCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.god", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        
        // Check if a target player is specified
        if (args.length > 0 && sender.hasPermission("frizzlenessentials.god.others")) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        } else {
            player = getPlayer(sender);
        }
        
        // Toggle god mode
        boolean godMode = !player.isInvulnerable();
        player.setInvulnerable(godMode);
        
        // Send messages
        String status = godMode ? "enabled" : "disabled";
        
        if (sender == player) {
            MessageUtils.sendConfigMessage(player, "messages.god.toggled", 
                    "God mode {status}.", "status", status);
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.god.toggled-other", 
                    "God mode {status} for {player}.", 
                    "status", status, 
                    "player", player.getName());
            
            MessageUtils.sendConfigMessage(player, "messages.god.toggled-by-other", 
                    "Your god mode has been {status} by {player}.", 
                    "status", status, 
                    "player", sender.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.god.others")) {
            String partialName = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 