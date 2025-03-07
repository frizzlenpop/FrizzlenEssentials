package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LastDeathCommand extends BaseCommand {
    
    public LastDeathCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.lastdeath", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get the player's last death location
        Location deathLocation = plugin.getTeleportManager().getDeathLocation(player.getUniqueId());
        
        if (deathLocation == null) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.no-death-location", 
                    "You have no recorded death location.");
            return true;
        }
        
        // Teleport the player to their death location
        boolean success = plugin.getTeleportManager().teleport(player, deathLocation);
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.teleported-to-death", 
                    "Teleported to your last death location.");
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 