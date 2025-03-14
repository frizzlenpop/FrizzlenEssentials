package com.frizzlenpop.frizzlenessentials.commands.utility;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NickCommand extends BaseCommand {
    
    private static final List<String> NICK_REMOVE_OPTIONS = Arrays.asList("off", "reset", "clear", "remove");
    
    public NickCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.nick", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        Player target = player; // Default to self
        String nickname = null;
        
        // Handle different argument cases
        if (args.length == 0) {
            // Display current nickname
            showCurrentNickname(player, player);
            return true;
        } else if (args.length == 1) {
            // Set nickname for self or reset
            if (NICK_REMOVE_OPTIONS.contains(args[0].toLowerCase())) {
                // Remove nickname
                removeNickname(player, player);
            } else {
                // Set own nickname
                nickname = args[0];
                setNickname(player, player, nickname);
            }
            return true;
        } else if (args.length >= 2 && sender.hasPermission("frizzlenessentials.nick.others")) {
            // Set nickname for another player
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
            
            if (NICK_REMOVE_OPTIONS.contains(args[1].toLowerCase())) {
                // Remove nickname for target
                removeNickname(player, target);
            } else {
                // Set nickname for target
                nickname = args[1];
                setNickname(player, target, nickname);
            }
            return true;
        } else {
            // Not enough permissions for others
            MessageUtils.sendConfigMessage(sender, "messages.errors.no-permission", 
                    "You don't have permission to change other players' nicknames.");
            return true;
        }
    }
    
    /**
     * Shows the current nickname for a player
     * 
     * @param sender The command sender
     * @param target The player to check nickname for
     */
    private void showCurrentNickname(CommandSender sender, Player target) {
        String nickname = getNickname(target);
        
        if (nickname == null || nickname.isEmpty()) {
            if (sender == target) {
                MessageUtils.sendConfigMessage(sender, "messages.nick.no-nickname", 
                        "You don't have a nickname set.");
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.nick.no-nickname-other", 
                        "{player} doesn't have a nickname set.", 
                        "player", target.getName());
            }
        } else {
            if (sender == target) {
                MessageUtils.sendConfigMessage(sender, "messages.nick.current-nickname", 
                        "Your current nickname is: {nickname}", 
                        "nickname", nickname);
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.nick.current-nickname-other", 
                        "{player}'s current nickname is: {nickname}", 
                        "player", target.getName(), 
                        "nickname", nickname);
            }
        }
    }
    
    /**
     * Sets a nickname for a player
     * 
     * @param sender The command sender
     * @param target The player to set nickname for
     * @param nickname The nickname to set
     */
    private void setNickname(CommandSender sender, Player target, String nickname) {
        // Check if nickname is valid
        if (nickname.length() > 32) {
            MessageUtils.sendConfigMessage(sender, "messages.nick.too-long", 
                    "Nickname is too long (max 32 characters).");
            return;
        }
        
        // Check for inappropriate content if needed
        // Implementation would depend on plugin requirements
        
        // Translate color codes if sender has permission
        if (sender.hasPermission("frizzlenessentials.nick.color")) {
            nickname = ChatColor.translateAlternateColorCodes('&', nickname);
        }
        
        // Save the nickname
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = target.getUniqueId().toString();
        playersConfig.set("players." + uuid + ".nickname", nickname);
        plugin.getConfigManager().savePlayersConfig();
        plugin.getConfigManager().saveConfigs();
        
        // Update display name
        target.setDisplayName(nickname);
        
        // Send confirmation messages
        if (sender == target) {
            MessageUtils.sendConfigMessage(sender, "messages.nick.set", 
                    "Your nickname has been set to: {nickname}", 
                    "nickname", nickname);
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.nick.set-other", 
                    "You have set {player}'s nickname to: {nickname}", 
                    "player", target.getName(), 
                    "nickname", nickname);
            
            MessageUtils.sendConfigMessage(target, "messages.nick.set-by-other", 
                    "Your nickname has been set to {nickname} by {player}", 
                    "nickname", nickname, 
                    "player", sender.getName());
        }
    }
    
    /**
     * Removes a nickname from a player
     * 
     * @param sender The command sender
     * @param target The player to remove nickname from
     */
    private void removeNickname(CommandSender sender, Player target) {
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = target.getUniqueId().toString();
        
        // Check if player has a nickname set
        if (!playersConfig.contains("players." + uuid + ".nickname")) {
            if (sender == target) {
                MessageUtils.sendConfigMessage(sender, "messages.nick.no-nickname", 
                        "You don't have a nickname set.");
            } else {
                MessageUtils.sendConfigMessage(sender, "messages.nick.no-nickname-other", 
                        "{player} doesn't have a nickname set.", 
                        "player", target.getName());
            }
            return;
        }
        
        // Remove the nickname
        playersConfig.set("players." + uuid + ".nickname", null);
        plugin.getConfigManager().savePlayersConfig();
        plugin.getConfigManager().saveConfigs();
        
        // Reset display name
        target.setDisplayName(target.getName());
        
        // Send confirmation messages
        if (sender == target) {
            MessageUtils.sendConfigMessage(sender, "messages.nick.removed", 
                    "Your nickname has been removed.");
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.nick.removed-other", 
                    "You have removed {player}'s nickname.", 
                    "player", target.getName());
            
            MessageUtils.sendConfigMessage(target, "messages.nick.removed-by-other", 
                    "Your nickname has been removed by {player}", 
                    "player", sender.getName());
        }
    }
    
    /**
     * Gets the current nickname for a player
     * 
     * @param player The player to get nickname for
     * @return The nickname, or null if not set
     */
    private String getNickname(Player player) {
        if (player == null) return null;
        
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        return playersConfig.getString("players." + uuid + ".nickname");
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("frizzlenessentials.nick.others")) {
                // Suggest player names and removal options
                String partial = args[0].toLowerCase();
                
                // Add player names
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .collect(Collectors.toList()));
                
                // Add removal options
                for (String option : NICK_REMOVE_OPTIONS) {
                    if (option.startsWith(partial)) {
                        completions.add(option);
                    }
                }
            } else {
                // Just suggest removal options for regular users
                String partial = args[0].toLowerCase();
                for (String option : NICK_REMOVE_OPTIONS) {
                    if (option.startsWith(partial)) {
                        completions.add(option);
                    }
                }
            }
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.nick.others")) {
            // Suggest removal options for the target player
            String partial = args[1].toLowerCase();
            for (String option : NICK_REMOVE_OPTIONS) {
                if (option.startsWith(partial)) {
                    completions.add(option);
                }
            }
        }
        
        return completions;
    }
} 