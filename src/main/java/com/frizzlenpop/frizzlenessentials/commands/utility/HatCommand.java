package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class HatCommand extends BaseCommand {
    
    public HatCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.hat", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        PlayerInventory inventory = player.getInventory();
        
        // Get the item in the player's main hand
        ItemStack handItem = inventory.getItemInMainHand();
        
        // Check if the player is holding an item
        if (handItem.getType() == Material.AIR) {
            MessageUtils.sendConfigMessage(player, "messages.hat.no-item", 
                    "You must be holding an item to use this command.");
            return true;
        }
        
        // Get the current helmet
        ItemStack helmet = inventory.getHelmet();
        
        // Set the hand item as the helmet
        inventory.setHelmet(handItem);
        
        // Put the old helmet in the player's hand
        inventory.setItemInMainHand(helmet != null ? helmet : new ItemStack(Material.AIR));
        
        // Send success message
        MessageUtils.sendConfigMessage(player, "messages.hat.equipped", 
                "Item equipped as hat.");
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 