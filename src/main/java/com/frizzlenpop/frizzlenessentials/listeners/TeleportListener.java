package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.configuration.file.FileConfiguration;

public class TeleportListener implements Listener {
    
    private final FrizzlenEssentials plugin;
    
    public TeleportListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();
        
        // Skip if the locations are in the same block
        if (isSameBlock(fromLocation, toLocation)) {
            return;
        }
        
        // Check if we should save the last location
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        if (config.getBoolean("teleportation.save-last-location", true)) {
            // Skip certain teleport causes if configured
            PlayerTeleportEvent.TeleportCause cause = event.getCause();
            
            // You can add configuration options to skip certain teleport causes
            // For example, you might want to skip ENDER_PEARL or CHORUS_FRUIT teleports
            boolean skipCause = false;
            
            if (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && 
                    config.getBoolean("teleportation.skip-ender-pearl", true)) {
                skipCause = true;
            }
            
            if (cause == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT && 
                    config.getBoolean("teleportation.skip-chorus-fruit", true)) {
                skipCause = true;
            }
            
            if (!skipCause) {
                // Save the location to the back stack
                plugin.getLocationManager().addBackLocation(player, fromLocation);
            }
        }
    }
    
    /**
     * Check if two locations are in the same block
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     * @return True if the locations are in the same block, false otherwise
     */
    private boolean isSameBlock(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return false;
        if (loc1.getWorld() != loc2.getWorld()) return false;
        
        return loc1.getBlockX() == loc2.getBlockX() &&
               loc1.getBlockY() == loc2.getBlockY() &&
               loc1.getBlockZ() == loc2.getBlockZ();
    }
} 