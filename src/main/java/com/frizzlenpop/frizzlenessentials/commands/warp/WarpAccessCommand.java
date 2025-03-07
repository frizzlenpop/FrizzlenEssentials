package com.frizzlenpop.frizzlenessentials.commands.warp;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WarpAccessCommand extends BaseCommand {
    
    public WarpAccessCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.warpaccess", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (!checkArgs(player, args, 2, "/warpaccess <warp> <add|remove|public|private> [player]")) {
            return true;
        }
        
        String warpName = args[0];
        String action = args[1].toLowerCase();
        
        // Check if the warp exists and get info
        Map<String, Object> warpInfo = plugin.getWarpManager().getWarpInfo(warpName);
        if (warpInfo.isEmpty()) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-not-found", 
                    "Warp {name} not found.", 
                    "name", warpName);
            return true;
        }
        
        // Check if the player has permission to modify access
        if (!player.hasPermission("frizzlenessentials.warpaccess.admin")) {
            String creator = (String) warpInfo.get("creator");
            if (creator == null || !creator.equals(player.getName())) {
                MessageUtils.sendConfigMessage(player, "messages.warp.not-owner", 
                        "You don't own this warp.");
                return true;
            }
        }
        
        switch (action) {
            case "public":
            case "private":
                boolean isPublic = action.equals("public");
                plugin.getWarpManager().modifyAccess(player, warpName, null, isPublic);
                MessageUtils.sendConfigMessage(player, isPublic ? "messages.warp.set-public" : "messages.warp.set-private", 
                        "Warp {name} is now {status}.", 
                        "name", warpName,
                        "status", isPublic ? "public" : "private");
                break;
                
            case "add":
            case "remove":
                if (!checkArgs(player, args, 3, "/warpaccess <warp> <add|remove> <player>")) {
                    return true;
                }
                
                String targetName = args[2];
                Player target = Bukkit.getPlayer(targetName);
                
                if (target == null) {
                    MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                            "Player not found.");
                    return true;
                }
                
                boolean grant = action.equals("add");
                boolean success = plugin.getWarpManager().modifyAccess(player, warpName, target, grant);
                
                if (success) {
                    MessageUtils.sendConfigMessage(player, 
                            grant ? "messages.warp.access-added" : "messages.warp.access-removed", 
                            "{action} {player} {preposition} warp {name} access list.", 
                            "action", grant ? "Added" : "Removed",
                            "player", target.getName(),
                            "preposition", grant ? "to" : "from",
                            "name", warpName);
                }
                break;
                
            default:
                MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                        "Usage: /warpaccess <warp> <add|remove|public|private> [player]");
                break;
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            Player player = getPlayer(sender);
            if (player != null) {
                // Add all warps that the player has access to modify
                for (String warpName : plugin.getWarpManager().getWarpList()) {
                    if (warpName.toLowerCase().startsWith(partial)) {
                        Map<String, Object> warpInfo = plugin.getWarpManager().getWarpInfo(warpName);
                        if (!warpInfo.isEmpty() && (
                                player.hasPermission("frizzlenessentials.warpaccess.admin") ||
                                player.getName().equals(warpInfo.get("creator"))
                        )) {
                            completions.add(warpName);
                        }
                    }
                }
            }
        } else if (args.length == 2) {
            String partial = args[1].toLowerCase();
            List<String> actions = List.of("add", "remove", "public", "private");
            completions.addAll(actions.stream()
                    .filter(action -> action.startsWith(partial))
                    .collect(Collectors.toList()));
        } else if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            String partial = args[2].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 