package com.frizzlenpop.frizzlenessentials.commands.home;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;

public class SetHomeCommand extends BaseCommand {
    
    public SetHomeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.sethome", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get the home name (default to "home" if not specified)
        String homeName = args.length > 0 ? args[0].toLowerCase() : "home";
        
        // Check if they've reached their home limit
        Map<String, Location> homes = plugin.getHomeManager().getHomes(player);
        int maxHomes = plugin.getHomeManager().getMaxHomes(player);
        
        // Only check limit if they're not overriding an existing home
        if (!homes.containsKey(homeName) && homes.size() >= maxHomes) {
            MessageUtils.sendConfigMessage(player, "messages.home.max-homes-reached", 
                    "You have reached your maximum number of homes ({count}).", 
                    "count", String.valueOf(maxHomes));
            return true;
        }
        
        // Set the home location
        boolean success = plugin.getHomeManager().setHome(player, homeName, player.getLocation());
        
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
            
            // Always suggest "home" if it matches
            if ("home".startsWith(partialName)) {
                completions.add("home");
            }
        }
        
        return completions;
    }
} 