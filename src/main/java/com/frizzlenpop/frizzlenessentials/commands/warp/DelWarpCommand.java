package com.frizzlenpop.frizzlenessentials.commands.warp;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand extends BaseCommand {
    
    public DelWarpCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.delwarp", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if the command has the correct number of arguments
        if (!checkArgs(player, args, 1, "/delwarp <name>")) {
            return true;
        }
        
        String warpName = args[0];
        
        // Delete the warp
        boolean success = plugin.getWarpManager().deleteWarp(player, warpName);
        
        // The warp manager will send appropriate messages on success/failure
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            String partialName = args[0].toLowerCase();
            
            // If player has admin permission, show all warps
            if (player.hasPermission("frizzlenessentials.delwarp.others")) {
                // Get all warps
                List<String> allWarps = plugin.getWarpManager().getWarpList();
                
                // Filter warps by the partial name
                for (String warpName : allWarps) {
                    if (warpName.toLowerCase().startsWith(partialName)) {
                        completions.add(warpName);
                    }
                }
            } else {
                // Only show accessible warps
                List<String> accessibleWarps = plugin.getWarpManager().getAccessibleWarps(player);
                
                // Filter warps by the partial name
                for (String warpName : accessibleWarps) {
                    if (warpName.toLowerCase().startsWith(partialName)) {
                        completions.add(warpName);
                    }
                }
            }
        }
        
        return completions;
    }
} 