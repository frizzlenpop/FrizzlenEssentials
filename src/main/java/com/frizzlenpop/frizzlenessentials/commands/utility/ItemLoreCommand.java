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

public class ItemLoreCommand extends BaseCommand {
    
    public ItemLoreCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.itemlore", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /itemlore <add|set|clear> [line] [text]");
            return true;
        }
        
        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if the player is holding an item
        if (item.getType() == Material.AIR) {
            MessageUtils.sendConfigMessage(player, "messages.itemlore.no-item", 
                    "You must be holding an item to use this command.");
            return true;
        }
        
        // Get or create item meta
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            MessageUtils.sendConfigMessage(player, "messages.itemlore.cannot-modify", 
                    "This item's lore cannot be modified.");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "clear":
                // Clear all lore
                meta.setLore(null);
                item.setItemMeta(meta);
                
                MessageUtils.sendConfigMessage(player, "messages.itemlore.cleared", 
                        "Item lore cleared.");
                break;
                
            case "add":
                if (args.length < 2) {
                    MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                            "Usage: /itemlore add <text>");
                    return true;
                }
                
                // Get current lore or create new list
                List<String> currentLore = meta.getLore();
                if (currentLore == null) {
                    currentLore = new ArrayList<>();
                }
                
                // Join remaining arguments and translate color codes
                String newLine = ChatColor.translateAlternateColorCodes('&', 
                        String.join(" ", args).substring(args[0].length() + 1));
                
                // Add the new line
                currentLore.add(newLine);
                meta.setLore(currentLore);
                item.setItemMeta(meta);
                
                MessageUtils.sendConfigMessage(player, "messages.itemlore.added", 
                        "Added lore line: {line}", "line", newLine);
                break;
                
            case "set":
                if (args.length < 3) {
                    MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                            "Usage: /itemlore set <line> <text>");
                    return true;
                }
                
                try {
                    int lineNumber = Integer.parseInt(args[1]) - 1; // Convert to 0-based index
                    if (lineNumber < 0) {
                        MessageUtils.sendConfigMessage(player, "messages.itemlore.invalid-line", 
                                "Line number must be greater than 0.");
                        return true;
                    }
                    
                    // Get current lore or create new list
                    List<String> lore = meta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }
                    
                    // Ensure the list is long enough
                    while (lore.size() <= lineNumber) {
                        lore.add("");
                    }
                    
                    // Join remaining arguments and translate color codes
                    String text = ChatColor.translateAlternateColorCodes('&', 
                            String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                    
                    // Set the line
                    lore.set(lineNumber, text);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    
                    MessageUtils.sendConfigMessage(player, "messages.itemlore.set", 
                            "Set lore line {line} to: {text}", 
                            "line", String.valueOf(lineNumber + 1),
                            "text", text);
                    
                } catch (NumberFormatException e) {
                    MessageUtils.sendConfigMessage(player, "messages.itemlore.invalid-line", 
                            "Invalid line number.");
                    return true;
                }
                break;
                
            default:
                MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                        "Usage: /itemlore <add|set|clear> [line] [text]");
                break;
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String sub : new String[]{"add", "set", "clear"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            String partial = args[1].toLowerCase();
            // Suggest line numbers 1-5
            for (String line : new String[]{"1", "2", "3", "4", "5"}) {
                if (line.startsWith(partial)) {
                    completions.add(line);
                }
            }
        }
        
        return completions;
    }
} 