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

public class HomeCommand extends BaseCommand {
    
    public HomeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.home", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get the home name (default to "home" if not specified)
        String homeName = args.length > 0 ? args[0].toLowerCase() : "home";
        
        // Get the home location
        Location homeLocation = plugin.getHomeManager().getHome(player, homeName);
        
        if (homeLocation == null) {
            if (args.length > 0) {
                MessageUtils.sendConfigMessage(player, "messages.home.home-not-found", 
                        "Home {name} not found.", "name", homeName);
            } else {
                MessageUtils.sendConfigMessage(player, "messages.home.no-home-set", 
                        "You have not set a home yet.");
            }
            return true;
        }
        
        // Attempt to teleport the player
        boolean success = plugin.getTeleportManager().teleport(player, homeLocation);
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.home.teleported", 
                    "Teleported to home{name}.", 
                    "name", homeName.equals("home") ? "" : " " + homeName);
        }
        
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
            
            // Always suggest "home" if it matches
            if ("home".startsWith(partialName)) {
                completions.add("home");
            }
        }
        
        return completions;
    }
} 