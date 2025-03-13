package com.frizzlenpop.frizzlenessentials.managers;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnManager {
    
    private final FrizzlenEssentials plugin;
    private Location spawnLocation;
    private final boolean teleportOnJoin;
    private final boolean teleportOnRespawn;
    
    public SpawnManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        
        // Load settings from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.teleportOnJoin = config.getBoolean("spawn.teleport-on-join", false);
        this.teleportOnRespawn = config.getBoolean("spawn.teleport-on-respawn", true);
        
        // Load spawn location
        loadSpawn();
    }
    
    /**
     * Load spawn location from config
     */
    private void loadSpawn() {
        FileConfiguration spawnConfig = plugin.getConfigManager().getSpawnConfig();
        
        ConfigurationSection spawnSection = spawnConfig.getConfigurationSection("spawn");
        if (spawnSection != null) {
            spawnLocation = deserializeLocation(spawnSection);
            
            if (spawnLocation != null) {
                plugin.getLogger().info("Loaded spawn location: " + formatLocation(spawnLocation));
            } else {
                plugin.getLogger().warning("Failed to load spawn location from config.");
            }
        } else {
            plugin.getLogger().info("No spawn location set.");
        }
    }
    
    /**
     * Save spawn location to config
     */
    public void saveSpawn() {
        FileConfiguration spawnConfig = plugin.getConfigManager().getSpawnConfig();
        
        if (spawnLocation != null) {
            serializeLocation(spawnConfig, "spawn", spawnLocation);
            plugin.getLogger().info("Saved spawn location: " + formatLocation(spawnLocation));
        } else {
            spawnConfig.set("spawn", null);
            plugin.getLogger().info("Cleared spawn location.");
        }
        
        // Save the spawn config file immediately to ensure it's persisted to disk
        plugin.getConfigManager().saveSpawnConfig();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
    }
    
    /**
     * Serialize a location to config
     * 
     * @param config The config to save to
     * @param path The path to save at
     * @param location The location to serialize
     */
    private void serializeLocation(FileConfiguration config, String path, Location location) {
        if (location == null || location.getWorld() == null) return;
        
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
    }
    
    /**
     * Deserialize a location from config
     * 
     * @param section The config section containing the location data
     * @return The deserialized location, or null if invalid
     */
    private Location deserializeLocation(ConfigurationSection section) {
        if (section == null) return null;
        
        String worldName = section.getString("world");
        if (worldName == null) return null;
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * Format a location for logging
     * 
     * @param location The location to format
     * @return A formatted string representation of the location
     */
    private String formatLocation(Location location) {
        if (location == null || location.getWorld() == null) return "null";
        
        return String.format("%s, %.2f, %.2f, %.2f", 
                location.getWorld().getName(), 
                location.getX(), 
                location.getY(), 
                location.getZ());
    }
    
    /**
     * Set the spawn location
     * 
     * @param location The new spawn location
     */
    public void setSpawn(Location location) {
        if (location == null) return;
        
        this.spawnLocation = location.clone();
        saveSpawn();
    }
    
    /**
     * Get the spawn location
     * 
     * @return The spawn location, or null if not set
     */
    public Location getSpawn() {
        return spawnLocation != null ? spawnLocation.clone() : null;
    }
    
    /**
     * Check if a spawn location is set
     * 
     * @return True if a spawn location is set, false otherwise
     */
    public boolean isSpawnSet() {
        return spawnLocation != null;
    }
    
    /**
     * Teleport a player to spawn
     * 
     * @param player The player to teleport
     * @return True if the teleport was successful, false otherwise
     */
    public boolean teleportToSpawn(Player player) {
        if (player == null || !isSpawnSet()) return false;
        
        return player.teleport(spawnLocation);
    }
    
    /**
     * Check if players should be teleported to spawn on join
     * 
     * @return True if players should be teleported to spawn on join, false otherwise
     */
    public boolean shouldTeleportOnJoin() {
        return teleportOnJoin && isSpawnSet();
    }
    
    /**
     * Check if players should be teleported to spawn on respawn
     * 
     * @return True if players should be teleported to spawn on respawn, false otherwise
     */
    public boolean shouldTeleportOnRespawn() {
        return teleportOnRespawn && isSpawnSet();
    }
    
    /**
     * Remove the spawn location
     */
    public void removeSpawn() {
        this.spawnLocation = null;
        saveSpawn();
    }
} 