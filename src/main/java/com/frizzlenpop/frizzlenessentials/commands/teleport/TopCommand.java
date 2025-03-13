package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopCommand extends BaseCommand {
    
    public TopCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.top", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Get player's current location
        Location currentLocation = player.getLocation();
        World world = currentLocation.getWorld();
        
        int x = currentLocation.getBlockX();
        int z = currentLocation.getBlockZ();
        
        // Start from the world's max height and work our way down
        int maxY = world.getMaxHeight() - 1;
        Block block = null;
        
        // Find the highest non-air block
        for (int y = maxY; y >= 0; y--) {
            block = world.getBlockAt(x, y, z);
            if (!block.getType().isAir()) {
                break;
            }
        }
        
        if (block == null || block.getType().isAir()) {
            MessageUtils.sendConfigMessage(player, "messages.top.no-blocks-above", 
                    "There are no blocks above your position.");
            return true;
        }
        
        // Teleport the player to the top of the highest block
        Location topLocation = block.getLocation().clone();
        topLocation.setY(block.getY() + 1); // Stand on top of the block
        topLocation.setX(currentLocation.getX()); // Keep exact X coordinate
        topLocation.setZ(currentLocation.getZ()); // Keep exact Z coordinate
        topLocation.setPitch(currentLocation.getPitch()); // Keep the player's pitch
        topLocation.setYaw(currentLocation.getYaw()); // Keep the player's yaw
        
        // Record current location for /back command if needed
        plugin.getLocationManager().addBackLocation(player, currentLocation);
        
        // Teleport the player
        player.teleport(topLocation);
        
        MessageUtils.sendConfigMessage(player, "messages.top.teleported", 
                "Teleported to the highest block at your position.");
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completion for this command
        return new ArrayList<>();
    }
} 