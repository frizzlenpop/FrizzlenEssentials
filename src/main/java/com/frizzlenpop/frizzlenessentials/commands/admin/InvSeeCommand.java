package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvSeeCommand extends BaseCommand {
    
    public InvSeeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.invsee", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /invsee <player>");
            return true;
        }
        
        // Get target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // Check if player is trying to view their own inventory
        if (target == player) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-target-self", 
                    "You cannot view your own inventory with this command.");
            return true;
        }
        
        // Check if target is exempt from invsee
        if (target.hasPermission("frizzlenessentials.invsee.exempt") && 
                !player.hasPermission("frizzlenessentials.invsee.bypass")) {
            MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                    "You cannot view this player's inventory.");
            return true;
        }
        
        // Create inventory view
        Inventory inventory = Bukkit.createInventory(target, 36, target.getName() + "'s Inventory");
        
        // Copy inventory contents
        inventory.setContents(target.getInventory().getContents());
        
        // Open inventory for player
        player.openInventory(inventory);
        
        MessageUtils.sendConfigMessage(player, "messages.admin.invsee.opened", 
                "Opened {player}'s inventory.", 
                "player", target.getName());
        
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