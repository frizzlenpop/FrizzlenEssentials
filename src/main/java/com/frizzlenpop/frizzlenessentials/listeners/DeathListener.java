package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {
    
    private final FrizzlenEssentials plugin;
    
    public DeathListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        
        // Save death location
        plugin.getLocationManager().setDeathLocation(player, deathLocation);
        
        // Add to back locations if enabled
        if (plugin.getConfigManager().getMainConfig().getBoolean("teleportation.save-last-location", true)) {
            plugin.getLocationManager().addBackLocation(player, deathLocation);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // Check if player should be teleported to spawn
        if (plugin.getSpawnManager().shouldTeleportOnRespawn() && !event.isBedSpawn() && !event.isAnchorSpawn()) {
            Location spawnLocation = plugin.getSpawnManager().getSpawn();
            if (spawnLocation != null) {
                event.setRespawnLocation(spawnLocation);
            }
        }
    }
} 