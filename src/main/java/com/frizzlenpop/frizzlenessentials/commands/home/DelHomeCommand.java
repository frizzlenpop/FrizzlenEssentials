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

public class DelHomeCommand extends BaseCommand {
    
    public DelHomeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.delhome", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /delhome <name>");
            return true;
        }
        
        // Get the home name
        String homeName = args[0].toLowerCase();
        
        // Try to delete the home
        boolean success = plugin.getHomeManager().deleteHome(player, homeName);
        
        // The home manager will send appropriate messages on success/failure
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            String partialName = args[0].toLowerCase();
            
            // Get all homes for the player
            Map<String, Location> homes = plugin.getHomeManager().getHomes(player);
            
            // Filter homes by the partial name
            for (String homeName : homes.keySet()) {
                if (homeName.toLowerCase().startsWith(partialName)) {
                    completions.add(homeName);
                }
            }
        }
        
        return completions;
    }
} 