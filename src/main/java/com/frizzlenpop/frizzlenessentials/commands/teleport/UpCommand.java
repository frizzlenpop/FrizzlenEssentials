package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UpCommand extends BaseCommand {
    
    private final int DEFAULT_UP_DISTANCE = 10;
    private final int MAX_UP_DISTANCE = 100;
    
    public UpCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.up", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Determine distance to go up
        int distance = DEFAULT_UP_DISTANCE;
        
        if (args.length > 0) {
            try {
                distance = Integer.parseInt(args[0]);
                
                // Validate the distance
                if (distance <= 0) {
                    MessageUtils.sendConfigMessage(player, "messages.up.invalid-distance", 
                            "Distance must be greater than 0.");
                    return true;
                }
                
                // Check if the distance exceeds the maximum
                if (distance > MAX_UP_DISTANCE && !player.hasPermission("frizzlenessentials.up.unlimited")) {
                    MessageUtils.sendConfigMessage(player, "messages.up.max-distance-exceeded", 
                            "Maximum distance is {max}. Using that instead.", 
                            "max", String.valueOf(MAX_UP_DISTANCE));
                    distance = MAX_UP_DISTANCE;
                }
                
            } catch (NumberFormatException e) {
                MessageUtils.sendConfigMessage(player, "messages.errors.not-a-number", 
                        "'{arg}' is not a valid number.", 
                        "arg", args[0]);
                return true;
            }
        }
        
        // Get player's current location
        Location currentLocation = player.getLocation();
        World world = currentLocation.getWorld();
        
        // Calculate new Y position
        int newY = currentLocation.getBlockY() + distance;
        
        // Make sure we don't go beyond the world height
        if (newY >= world.getMaxHeight() - 1) {
            newY = world.getMaxHeight() - 2;
            MessageUtils.sendConfigMessage(player, "messages.up.max-height-reached", 
                    "Maximum world height reached. Teleporting to highest possible position.");
        }
        
        // Create the target location
        Location targetLocation = currentLocation.clone();
        targetLocation.setY(newY);
        
        // Record current location for /back command
        plugin.getLocationManager().addBackLocation(player, currentLocation);
        
        // Teleport the player
        player.teleport(targetLocation);
        
        // Place a glass block underneath them
        Location blockLocation = targetLocation.clone();
        blockLocation.setY(newY - 1);
        world.getBlockAt(blockLocation).setType(Material.GLASS);
        
        MessageUtils.sendConfigMessage(player, "messages.up.teleported", 
                "Teleported {distance} blocks upward.", 
                "distance", String.valueOf(distance));
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Tab completion for the distance argument
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            
            // Suggest some common distances
            for (String suggestion : new String[]{"5", "10", "20", "50", "100"}) {
                if (suggestion.startsWith(partial)) {
                    completions.add(suggestion);
                }
            }
        }
        
        return completions;
    }
} 