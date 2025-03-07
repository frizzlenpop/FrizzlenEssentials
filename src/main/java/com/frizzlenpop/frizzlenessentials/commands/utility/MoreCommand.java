package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MoreCommand extends BaseCommand {
    
    public MoreCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.more", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if the player is holding an item
        if (item.getType() == Material.AIR) {
            MessageUtils.sendConfigMessage(player, "messages.more.no-item", 
                    "You must be holding an item to use this command.");
            return true;
        }
        
        // Check if the item is stackable
        if (item.getMaxStackSize() == 1) {
            MessageUtils.sendConfigMessage(player, "messages.more.not-stackable", 
                    "This item cannot be stacked.");
            return true;
        }
        
        // Set the item amount to its maximum stack size
        item.setAmount(item.getMaxStackSize());
        
        // Send success message
        MessageUtils.sendConfigMessage(player, "messages.more.filled", 
                "Item stack filled to maximum size ({amount}).", 
                "amount", String.valueOf(item.getMaxStackSize()));
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 