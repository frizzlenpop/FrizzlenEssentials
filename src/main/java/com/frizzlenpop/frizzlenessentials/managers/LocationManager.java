package com.frizzlenpop.frizzlenessentials.managers;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocationManager {
    
    private final FrizzlenEssentials plugin;
    private final Map<UUID, Deque<Location>> backLocations;
    private final Map<UUID, Location> lastDeathLocations;
    private final int maxBackLocations;
    
    public LocationManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        this.backLocations = new ConcurrentHashMap<>();
        this.lastDeathLocations = new ConcurrentHashMap<>();
        
        // Load settings from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.maxBackLocations = config.getInt("teleportation.max-back-locations", 5);
        
        // Load locations from config
        loadLocations();
    }
    
    /**
     * Load locations from config
     */
    private void loadLocations() {
        FileConfiguration locationsConfig = plugin.getConfigManager().getLocationsConfig();
        
        // Load death locations
        ConfigurationSection deathSection = locationsConfig.getConfigurationSection("death-locations");
        if (deathSection != null) {
            for (String uuidStr : deathSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Location location = deserializeLocation(deathSection.getConfigurationSection(uuidStr));
                    if (location != null) {
                        lastDeathLocations.put(uuid, location);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in death locations: " + uuidStr);
                }
            }
        }
        
        // Load back locations
        ConfigurationSection backSection = locationsConfig.getConfigurationSection("back-locations");
        if (backSection != null) {
            for (String uuidStr : backSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    ConfigurationSection playerSection = backSection.getConfigurationSection(uuidStr);
                    
                    if (playerSection != null) {
                        Deque<Location> locations = new LinkedList<>();
                        
                        // Get the list of indices
                        List<Integer> indices = new ArrayList<>();
                        for (String key : playerSection.getKeys(false)) {
                            try {
                                indices.add(Integer.parseInt(key));
                            } catch (NumberFormatException e) {
                                plugin.getLogger().warning("Invalid index in back locations: " + key);
                            }
                        }
                        
                        // Sort indices to maintain order
                        Collections.sort(indices);
                        
                        // Load locations in order
                        for (int index : indices) {
                            ConfigurationSection locSection = playerSection.getConfigurationSection(String.valueOf(index));
                            if (locSection != null) {
                                Location location = deserializeLocation(locSection);
                                if (location != null) {
                                    locations.add(location);
                                }
                            }
                        }
                        
                        if (!locations.isEmpty()) {
                            backLocations.put(uuid, locations);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in back locations: " + uuidStr);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + lastDeathLocations.size() + " death locations and " + backLocations.size() + " back location stacks.");
    }
    
    /**
     * Save all locations to config
     */
    public void saveAllLocations() {
        FileConfiguration locationsConfig = plugin.getConfigManager().getLocationsConfig();
        
        // Clear existing sections
        locationsConfig.set("death-locations", null);
        locationsConfig.set("back-locations", null);
        
        // Save death locations
        for (Map.Entry<UUID, Location> entry : lastDeathLocations.entrySet()) {
            String path = "death-locations." + entry.getKey().toString();
            serializeLocation(locationsConfig, path, entry.getValue());
        }
        
        // Save back locations
        for (Map.Entry<UUID, Deque<Location>> entry : backLocations.entrySet()) {
            UUID uuid = entry.getKey();
            Deque<Location> locations = entry.getValue();
            
            int index = 0;
            for (Location location : locations) {
                String path = "back-locations." + uuid.toString() + "." + index;
                serializeLocation(locationsConfig, path, location);
                index++;
            }
        }
        
        // Save the config
        plugin.getConfigManager().saveLocationsConfig();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        plugin.getLogger().info("Saved " + lastDeathLocations.size() + " death locations and " + backLocations.size() + " back location stacks.");
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
     * Set a player's death location
     * 
     * @param player The player
     * @param location The death location
     */
    public void setDeathLocation(Player player, Location location) {
        if (player == null || location == null) return;
        
        lastDeathLocations.put(player.getUniqueId(), location.clone());
        
        // Save location immediately to ensure persistence
        saveAllLocations();
    }
    
    /**
     * Get a player's death location
     * 
     * @param player The player
     * @return The death location, or null if not found
     */
    public Location getDeathLocation(Player player) {
        if (player == null) return null;
        
        Location location = lastDeathLocations.get(player.getUniqueId());
        return location != null ? location.clone() : null;
    }
    
    /**
     * Add a location to a player's back stack
     * 
     * @param player The player
     * @param location The location to add
     */
    public void addBackLocation(Player player, Location location) {
        if (player == null || location == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Get or create deque
        Deque<Location> locations = backLocations.computeIfAbsent(uuid, k -> new LinkedList<>());
        
        // Add location to the front
        locations.addFirst(location.clone());
        
        // Enforce max back locations
        while (locations.size() > maxBackLocations) {
            locations.removeLast();
        }
        
        // Save locations immediately to ensure persistence
        saveAllLocations();
    }
    
    /**
     * Get and remove the most recent back location for a player
     * 
     * @param player The player
     * @return The back location, or null if not found
     */
    public Location popBackLocation(Player player) {
        if (player == null) return null;
        
        UUID uuid = player.getUniqueId();
        
        // Check if the player has any back locations
        if (!backLocations.containsKey(uuid) || backLocations.get(uuid).isEmpty()) {
            return null;
        }
        
        // Get and remove the most recent location
        Deque<Location> locations = backLocations.get(uuid);
        Location location = locations.pop();
        
        // If the stack is now empty, remove it
        if (locations.isEmpty()) {
            backLocations.remove(uuid);
        }
        
        return location != null ? location.clone() : null;
    }
    
    /**
     * Get the most recent back location for a player without removing it
     * 
     * @param player The player
     * @return The back location, or null if not found
     */
    public Location peekBackLocation(Player player) {
        if (player == null) return null;
        
        UUID uuid = player.getUniqueId();
        
        // Check if the player has any back locations
        if (!backLocations.containsKey(uuid) || backLocations.get(uuid).isEmpty()) {
            return null;
        }
        
        // Get the most recent location without removing it
        Location location = backLocations.get(uuid).peek();
        return location != null ? location.clone() : null;
    }
    
    /**
     * Check if a player has any back locations
     * 
     * @param player The player
     * @return True if the player has back locations, false otherwise
     */
    public boolean hasBackLocation(Player player) {
        if (player == null) return false;
        
        UUID uuid = player.getUniqueId();
        return backLocations.containsKey(uuid) && !backLocations.get(uuid).isEmpty();
    }
    
    /**
     * Clear all back locations for a player
     * 
     * @param player The player
     */
    public void clearBackLocations(Player player) {
        if (player == null) return;
        
        backLocations.remove(player.getUniqueId());
    }
} 