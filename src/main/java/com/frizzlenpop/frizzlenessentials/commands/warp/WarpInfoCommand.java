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
import java.util.Map;

public class WarpInfoCommand extends BaseCommand {
    
    public WarpInfoCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.warpinfo", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if the command has the correct number of arguments
        if (!checkArgs(player, args, 1, "/warpinfo <name>")) {
            return true;
        }
        
        String warpName = args[0];
        
        // Get warp information
        Map<String, Object> warpInfo = plugin.getWarpManager().getWarpInfo(warpName);
        if (warpInfo.isEmpty()) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-not-found", 
                    "Warp {name} not found.", 
                    "name", warpName);
            return true;
        }
        
        // Display warp information
        player.sendMessage("§6Warp Information: §f" + warpName);
        player.sendMessage("§7- World: §f" + ((Location)warpInfo.get("location")).getWorld().getName());
        player.sendMessage("§7- Location: §f" + String.format("%.2f, %.2f, %.2f", 
                warpInfo.get("x"), 
                warpInfo.get("y"), 
                warpInfo.get("z")));
        player.sendMessage("§7- Creator: §f" + warpInfo.getOrDefault("creator", "Unknown"));
        
        // Display access information if player has permission
        if (player.hasPermission("frizzlenessentials.warpinfo.access")) {
            boolean isPublic = (boolean) warpInfo.getOrDefault("public", true);
            if (isPublic) {
                player.sendMessage("§7- Access: §fPublic");
            } else {
                @SuppressWarnings("unchecked")
                List<String> accessList = (List<String>) warpInfo.get("access");
                if (accessList != null && !accessList.isEmpty()) {
                    player.sendMessage("§7- Access: §fPrivate");
                    player.sendMessage("§7- Allowed Players:");
                    for (String allowedPlayer : accessList) {
                        player.sendMessage("  §7- §f" + allowedPlayer);
                    }
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();
            
            // Add all warps that match the partial name
            for (String warpName : plugin.getWarpManager().getWarpList()) {
                if (warpName.toLowerCase().startsWith(partial)) {
                    completions.add(warpName);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }
} 