package com.frizzlenpop.frizzlenessentials.commands.warp;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand extends BaseCommand {
    
    public WarpCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.warp", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if the command has the correct number of arguments
        if (!checkArgs(player, args, 1, "/warp <name>")) {
            return true;
        }
        
        String warpName = args[0];
        
        // Check if the warp exists
        Location warpLocation = plugin.getWarpManager().getWarp(warpName);
        if (warpLocation == null) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-not-found", "Warp {name} not found.", "name", warpName);
            return true;
        }
        
        // Check if the player has access to the warp
        if (!plugin.getWarpManager().hasAccess(player, warpName)) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-no-permission", "You don't have permission to use this warp.");
            return true;
        }
        
        // Teleport the player to the warp
        boolean success = plugin.getTeleportManager().teleport(player, warpLocation);
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.warp.teleport-to-warp", "Teleporting to warp {name}.", "name", warpName);
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            String partialName = args[0].toLowerCase();
            
            // Get a list of accessible warps for the player
            List<String> accessibleWarps = plugin.getWarpManager().getAccessibleWarps(player);
            
            // Filter warps by the partial name
            for (String warpName : accessibleWarps) {
                if (warpName.toLowerCase().startsWith(partialName)) {
                    completions.add(warpName);
                }
            }
        }
        
        return completions;
    }
} 