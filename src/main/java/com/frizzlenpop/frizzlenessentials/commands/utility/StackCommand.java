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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackCommand extends BaseCommand {
    
    public StackCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.stack", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        PlayerInventory inventory = player.getInventory();
        
        // Check if we're stacking all items or just similar ones
        boolean stackAll = args.length > 0 && args[0].equalsIgnoreCase("all") && 
                player.hasPermission("frizzlenessentials.stack.all");
        
        // Create a map to store similar items
        Map<String, List<Integer>> similarItems = new HashMap<>();
        
        // Scan inventory for items
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            
            // Skip items that are already at max stack size
            if (item.getAmount() >= item.getMaxStackSize()) continue;
            
            // Skip unstackable items unless stackAll is true
            if (!stackAll && item.getMaxStackSize() == 1) continue;
            
            // Create a key for similar items
            String key = createItemKey(item);
            
            // Add the slot to the list of similar items
            similarItems.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
        
        // Track how many items were stacked
        int stackedCount = 0;
        
        // Stack similar items
        for (List<Integer> slots : similarItems.values()) {
            if (slots.size() < 2) continue; // Need at least 2 items to stack
            
            // Get the first item as the target stack
            ItemStack target = inventory.getItem(slots.get(0));
            if (target == null) continue;
            
            // Try to stack items into the target
            for (int i = 1; i < slots.size(); i++) {
                ItemStack source = inventory.getItem(slots.get(i));
                if (source == null) continue;
                
                // Calculate how many items can be added to the target
                int space = target.getMaxStackSize() - target.getAmount();
                if (space <= 0) break;
                
                int toTransfer = Math.min(space, source.getAmount());
                
                // Update the stacks
                target.setAmount(target.getAmount() + toTransfer);
                
                if (toTransfer >= source.getAmount()) {
                    inventory.setItem(slots.get(i), null);
                } else {
                    source.setAmount(source.getAmount() - toTransfer);
                }
                
                stackedCount++;
            }
        }
        
        // Send appropriate message
        if (stackedCount > 0) {
            MessageUtils.sendConfigMessage(player, "messages.stack.stacked", 
                    "Stacked {count} item groups.", 
                    "count", String.valueOf(stackedCount));
        } else {
            MessageUtils.sendConfigMessage(player, "messages.stack.nothing-to-stack", 
                    "No items to stack.");
        }
        
        return true;
    }
    
    /**
     * Create a unique key for similar items
     * 
     * @param item The item to create a key for
     * @return A string key representing the item's important properties
     */
    private String createItemKey(ItemStack item) {
        StringBuilder key = new StringBuilder();
        key.append(item.getType().name());
        
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                key.append(":name=").append(item.getItemMeta().getDisplayName());
            }
            if (item.getItemMeta().hasLore()) {
                key.append(":lore=").append(String.join(",", item.getItemMeta().getLore()));
            }
            if (item.getItemMeta().hasEnchants()) {
                key.append(":enchants=").append(item.getEnchantments().toString());
            }
        }
        
        return key.toString();
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.stack.all")) {
            String partial = args[0].toLowerCase();
            if ("all".startsWith(partial)) {
                completions.add("all");
            }
        }
        
        return completions;
    }
} 