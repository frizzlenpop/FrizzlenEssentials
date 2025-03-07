package com.frizzlenpop.frizzlenessentials.commands.home;

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

public class ListHomesCommand extends BaseCommand {
    
    public ListHomesCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.listhomes", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get all homes for the player
        Map<String, Location> homes = plugin.getHomeManager().getHomes(player);
        
        if (homes.isEmpty()) {
            MessageUtils.sendConfigMessage(player, "messages.home.no-homes", 
                    "You have no homes set.");
            return true;
        }
        
        // Get the list of homes as a formatted string
        String homesList = plugin.getHomeManager().listHomes(player);
        
        // Send the list to the player
        MessageUtils.sendConfigMessage(player, "messages.home.homes-list", 
                "Your homes ({count}): {homes}", 
                "count", String.valueOf(homes.size()),
                "homes", homesList);
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 