package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VanishCommand extends BaseCommand {
    
    private static final String VANISH_METADATA = "vanished";
    
    public VanishCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.vanish", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get target player
        Player target = player;
        if (args.length > 0) {
            if (!player.hasPermission("frizzlenessentials.vanish.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to toggle vanish for other players.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Toggle vanish state
        boolean vanished = !target.hasMetadata(VANISH_METADATA);
        
        if (vanished) {
            // Hide player from everyone without permission to see vanished players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("frizzlenessentials.vanish.see")) {
                    onlinePlayer.hidePlayer(plugin, target);
                }
            }
            
            // Set metadata
            target.setMetadata(VANISH_METADATA, new FixedMetadataValue(plugin, true));
            
            // Send messages
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.vanish.enabled", 
                        "You are now vanished.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.vanish.enabled-other", 
                        "Made {player} vanish.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.vanish.enabled-by-other", 
                        "You were made vanish by {player}.", 
                        "player", player.getName());
            }
        } else {
            // Show player to everyone
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, target);
            }
            
            // Remove metadata
            target.removeMetadata(VANISH_METADATA, plugin);
            
            // Send messages
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.vanish.disabled", 
                        "You are no longer vanished.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.vanish.disabled-other", 
                        "Made {player} visible.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.vanish.disabled-by-other", 
                        "You were made visible by {player}.", 
                        "player", player.getName());
            }
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.vanish.others")) {
            String partial = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 