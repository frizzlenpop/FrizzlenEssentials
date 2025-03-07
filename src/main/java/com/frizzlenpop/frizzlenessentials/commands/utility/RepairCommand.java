package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class RepairCommand extends BaseCommand {
    
    public RepairCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.repair", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we're repairing all items
        boolean repairAll = args.length > 0 && args[0].equalsIgnoreCase("all") && 
                player.hasPermission("frizzlenessentials.repair.all");
        
        if (repairAll) {
            // Repair all items in inventory
            int repairedCount = 0;
            
            // Check main inventory
            for (ItemStack item : player.getInventory().getContents()) {
                if (repairItem(item)) {
                    repairedCount++;
                }
            }
            
            // Check armor contents
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (repairItem(item)) {
                    repairedCount++;
                }
            }
            
            // Check offhand
            if (repairItem(player.getInventory().getItemInOffHand())) {
                repairedCount++;
            }
            
            if (repairedCount > 0) {
                MessageUtils.sendConfigMessage(player, "messages.repair.repaired-all", 
                        "Repaired {count} items.", 
                        "count", String.valueOf(repairedCount));
            } else {
                MessageUtils.sendConfigMessage(player, "messages.repair.nothing-to-repair", 
                        "No items to repair.");
            }
            
        } else {
            // Repair only held item
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if (item.getType() == Material.AIR) {
                MessageUtils.sendConfigMessage(player, "messages.repair.no-item", 
                        "You must be holding an item to repair it.");
                return true;
            }
            
            if (repairItem(item)) {
                MessageUtils.sendConfigMessage(player, "messages.repair.repaired", 
                        "Item repaired successfully.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.repair.cannot-repair", 
                        "This item cannot be repaired.");
            }
        }
        
        return true;
    }
    
    /**
     * Attempt to repair an item
     * 
     * @param item The item to repair
     * @return True if the item was repaired, false otherwise
     */
    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        if (!(item.getItemMeta() instanceof Damageable)) {
            return false;
        }
        
        Damageable meta = (Damageable) item.getItemMeta();
        
        // Check if item is damaged
        if (!meta.hasDamage()) {
            return false;
        }
        
        // Repair the item
        meta.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.repair.all")) {
            String partial = args[0].toLowerCase();
            if ("all".startsWith(partial)) {
                completions.add("all");
            }
        }
        
        return completions;
    }
} 