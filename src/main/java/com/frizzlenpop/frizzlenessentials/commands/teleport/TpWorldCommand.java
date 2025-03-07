package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TpWorldCommand extends BaseCommand {
    
    public TpWorldCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tpworld", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /tpworld <world> [player]");
            return true;
        }
        
        // Get the target player (if specified)
        Player target = player;
        if (args.length > 1) {
            if (!player.hasPermission("frizzlenessentials.tpworld.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to teleport other players to worlds.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Try to teleport to the specified world
        boolean success = plugin.getTeleportManager().teleportToWorld(target, args[0]);
        
        if (success) {
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.teleport.world-teleported", 
                        "Teleported to world: {world}", 
                        "world", args[0]);
            } else {
                MessageUtils.sendConfigMessage(player, "messages.teleport.world-teleported-other", 
                        "Teleported {player} to world: {world}", 
                        "player", target.getName(),
                        "world", args[0]);
                
                MessageUtils.sendConfigMessage(target, "messages.teleport.world-teleported-by-other", 
                        "You were teleported to world {world} by {player}.", 
                        "world", args[0],
                        "player", player.getName());
            }
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest world names
            String partialWorld = args[0].toLowerCase();
            completions.addAll(Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialWorld))
                    .collect(Collectors.toList()));
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.tpworld.others")) {
            // Suggest player names
            String partialName = args[1].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 