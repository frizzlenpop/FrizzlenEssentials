package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemNameCommand extends BaseCommand {
    
    public ItemNameCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.itemname", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /itemname <name> (use 'clear' to remove name)");
            return true;
        }
        
        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if the player is holding an item
        if (item.getType() == Material.AIR) {
            MessageUtils.sendConfigMessage(player, "messages.itemname.no-item", 
                    "You must be holding an item to use this command.");
            return true;
        }
        
        // Get the item meta
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            MessageUtils.sendConfigMessage(player, "messages.itemname.cannot-rename", 
                    "This item cannot be renamed.");
            return true;
        }
        
        // Check if we're clearing the name
        if (args[0].equalsIgnoreCase("clear")) {
            meta.setDisplayName(null);
            item.setItemMeta(meta);
            
            MessageUtils.sendConfigMessage(player, "messages.itemname.cleared", 
                    "Item name cleared.");
            return true;
        }
        
        // Join all arguments with spaces and translate color codes
        String name = String.join(" ", args);
        name = ChatColor.translateAlternateColorCodes('&', name);
        
        // Set the display name
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        
        // Send success message
        MessageUtils.sendConfigMessage(player, "messages.itemname.renamed", 
                "Item renamed to: {name}", "name", name);
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if ("clear".startsWith(partial)) {
                completions.add("clear");
            }
        }
        
        return completions;
    }
} 