package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand extends BaseCommand {
    
    public FlyCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.fly", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if the command is targeting another player
        if (args.length > 0) {
            // Check if the player has permission to toggle flight for others
            if (!player.hasPermission("frizzlenessentials.fly.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", "You don't have permission to toggle flight for other players.");
                return true;
            }
            
            // Get the target player
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", "Player not found.");
                return true;
            }
            
            // Toggle flight for the target player
            toggleFlight(target);
            
            // Send messages
            if (target.getAllowFlight()) {
                MessageUtils.sendConfigMessage(player, "messages.player.flight-enabled-other", "Flight mode enabled for {player}.", "player", target.getName());
                MessageUtils.sendConfigMessage(target, "messages.player.flight-enabled", "Flight mode enabled by {player}.", "player", player.getName());
            } else {
                MessageUtils.sendConfigMessage(player, "messages.player.flight-disabled-other", "Flight mode disabled for {player}.", "player", target.getName());
                MessageUtils.sendConfigMessage(target, "messages.player.flight-disabled", "Flight mode disabled by {player}.", "player", player.getName());
            }
        } else {
            // Toggle flight for the player
            toggleFlight(player);
            
            // Send message
            if (player.getAllowFlight()) {
                MessageUtils.sendConfigMessage(player, "messages.player.flight-enabled", "Flight mode enabled.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.player.flight-disabled", "Flight mode disabled.");
            }
        }
        
        // Save flight state if persistent
        if (plugin.getConfigManager().getMainConfig().getBoolean("player-utilities.persistent-flight", false)) {
            saveFlightState(player.getAllowFlight() ? player : (args.length > 0 ? Bukkit.getPlayer(args[0]) : null));
        }
        
        return true;
    }
    
    /**
     * Toggle flight for a player
     * 
     * @param player The player to toggle flight for
     */
    private void toggleFlight(Player player) {
        if (player == null) return;
        
        boolean allowFlight = !player.getAllowFlight();
        player.setAllowFlight(allowFlight);
        
        if (allowFlight) {
            player.setFlying(true);
        }
    }
    
    /**
     * Save a player's flight state to config
     * 
     * @param player The player to save flight state for
     */
    private void saveFlightState(Player player) {
        if (player == null) return;
        
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        playersConfig.set("players." + uuid + ".flight", player.getAllowFlight());
        plugin.getConfigManager().savePlayersConfig();
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.fly.others")) {
            String partialName = args[0].toLowerCase();
            completions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
} 