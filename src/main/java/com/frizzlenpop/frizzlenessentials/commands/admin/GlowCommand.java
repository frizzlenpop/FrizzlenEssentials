package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlowCommand extends BaseCommand {
    
    public GlowCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.glow", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get target player
        Player target = player;
        if (args.length > 0) {
            if (!player.hasPermission("frizzlenessentials.glow.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to toggle glow for other players.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Toggle glow effect
        boolean glowing = !target.isGlowing();
        target.setGlowing(glowing);
        
        // Apply or remove glowing effect
        if (glowing) {
            // Add glowing effect
            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            
            // Send messages
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.glow.enabled", 
                        "You are now glowing.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.glow.enabled-other", 
                        "Made {player} glow.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.glow.enabled-by-other", 
                        "You were made to glow by {player}.", 
                        "player", player.getName());
            }
        } else {
            // Remove glowing effect
            target.removePotionEffect(PotionEffectType.GLOWING);
            
            // Send messages
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.glow.disabled", 
                        "You are no longer glowing.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.glow.disabled-other", 
                        "Removed glow from {player}.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.glow.disabled-by-other", 
                        "Your glow was removed by {player}.", 
                        "player", player.getName());
            }
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("frizzlenessentials.glow.others")) {
            String partial = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 