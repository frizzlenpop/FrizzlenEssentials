package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClearInventoryCommand extends BaseCommand {
    
    public ClearInventoryCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.clearinventory", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get target player
        Player target = player;
        if (args.length > 0) {
            if (!player.hasPermission("frizzlenessentials.clearinventory.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to clear other players' inventories.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Check if target is exempt from clear inventory
        if (target.hasPermission("frizzlenessentials.clearinventory.exempt") && 
                !player.hasPermission("frizzlenessentials.clearinventory.bypass")) {
            MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                    "You cannot clear this player's inventory.");
            return true;
        }
        
        // Count non-empty slots
        int count = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (item != null) count++;
        }
        
        // Clear inventory
        target.getInventory().clear();
        
        // Send messages
        if (target == player) {
            MessageUtils.sendConfigMessage(player, "messages.admin.clearinventory.self", 
                    "Cleared your inventory ({count} items).", 
                    "count", String.valueOf(count));
        } else {
            MessageUtils.sendConfigMessage(player, "messages.admin.clearinventory.other", 
                    "Cleared {player}'s inventory ({count} items).", 
                    "player", target.getName(),
                    "count", String.valueOf(count));
            
            MessageUtils.sendConfigMessage(target, "messages.admin.clearinventory.by-other", 
                    "Your inventory was cleared by {player} ({count} items).", 
                    "player", player.getName(),
                    "count", String.valueOf(count));
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.clearinventory.others")) {
            String partial = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 