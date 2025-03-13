package com.frizzlenpop.frizzlenessentials.managers;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    
    private final FrizzlenEssentials plugin;
    private final Map<UUID, Map<String, Location>> playerHomes;
    private final int defaultMaxHomes;
    private final List<Integer> maxHomesByPermission;
    private final boolean allowOverride;
    
    public HomeManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        this.playerHomes = new ConcurrentHashMap<>();
        
        // Load settings from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.defaultMaxHomes = config.getInt("homes.default-max-homes", 3);
        this.maxHomesByPermission = config.getIntegerList("homes.max-homes-by-permission");
        this.allowOverride = config.getBoolean("homes.allow-override", true);
        
        // Load homes from config
        loadHomes();
    }
    
    /**
     * Load homes from config
     */
    private void loadHomes() {
        FileConfiguration homesConfig = plugin.getConfigManager().getHomesConfig();
        
        ConfigurationSection playersSection = homesConfig.getConfigurationSection("players");
        if (playersSection == null) return;
        
        for (String uuidStr : playersSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidStr);
                
                if (playerSection != null) {
                    Map<String, Location> homes = new HashMap<>();
                    
                    for (String homeName : playerSection.getKeys(false)) {
                        ConfigurationSection homeSection = playerSection.getConfigurationSection(homeName);
                        if (homeSection != null) {
                            Location location = deserializeLocation(homeSection);
                            if (location != null) {
                                homes.put(homeName.toLowerCase(), location);
                            }
                        }
                    }
                    
                    if (!homes.isEmpty()) {
                        playerHomes.put(uuid, homes);
                    }
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in homes config: " + uuidStr);
            }
        }
        
        plugin.getLogger().info("Loaded " + playerHomes.size() + " players' homes.");
    }
    
    /**
     * Save all homes to config
     */
    public void saveAllHomes() {
        FileConfiguration homesConfig = plugin.getConfigManager().getHomesConfig();
        
        // Clear existing homes
        homesConfig.set("players", null);
        
        // Save all homes
        for (Map.Entry<UUID, Map<String, Location>> entry : playerHomes.entrySet()) {
            UUID uuid = entry.getKey();
            Map<String, Location> homes = entry.getValue();
            
            for (Map.Entry<String, Location> homeEntry : homes.entrySet()) {
                String homeName = homeEntry.getKey();
                Location location = homeEntry.getValue();
                
                String path = "players." + uuid.toString() + "." + homeName;
                serializeLocation(homesConfig, path, location);
            }
        }
        
        // Save the config
        plugin.getConfigManager().saveHomesConfig();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        plugin.getLogger().info("Saved " + playerHomes.size() + " players' homes.");
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
     * Set a home for a player
     * 
     * @param player The player
     * @param homeName The name of the home
     * @param location The location of the home
     * @return True if the home was set, false otherwise
     */
    public boolean setHome(Player player, String homeName, Location location) {
        if (player == null || homeName == null || location == null) return false;
        
        UUID uuid = player.getUniqueId();
        String name = homeName.toLowerCase();
        
        // Get or create the player's homes map
        Map<String, Location> homes = playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
        
        // Check if the player already has a home with this name
        boolean homeExists = homes.containsKey(name);
        
        // If the home exists and override is not allowed, return false
        if (homeExists && !allowOverride) {
            MessageUtils.sendConfigMessage(player, "messages.home.home-exists", "You already have a home named {name}.", "name", homeName);
            return false;
        }
        
        // Check if the player has reached their home limit
        if (!homeExists && homes.size() >= getMaxHomes(player)) {
            MessageUtils.sendConfigMessage(player, "messages.home.max-homes-reached", "You have reached your maximum number of homes ({count}).", "count", getMaxHomes(player));
            return false;
        }
        
        // Set the home
        homes.put(name, location);
        
        // Send appropriate message
        if (homeExists) {
            MessageUtils.sendConfigMessage(player, "messages.home.home-override", "Home {name} already exists. It has been overridden.", "name", homeName);
        } else {
            MessageUtils.sendConfigMessage(player, "messages.home.home-set", "Home {name} has been set.", "name", homeName);
        }
        
        // Save the homes immediately to ensure persistence
        saveAllHomes();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        return true;
    }
    
    /**
     * Delete a home for a player
     * 
     * @param player The player
     * @param homeName The name of the home
     * @return True if the home was deleted, false otherwise
     */
    public boolean deleteHome(Player player, String homeName) {
        if (player == null || homeName == null) return false;
        
        UUID uuid = player.getUniqueId();
        String name = homeName.toLowerCase();
        
        // Check if the player has any homes
        if (!playerHomes.containsKey(uuid)) {
            MessageUtils.sendConfigMessage(player, "messages.home.no-homes", "You have no homes set.");
            return false;
        }
        
        Map<String, Location> homes = playerHomes.get(uuid);
        
        // Check if the home exists
        if (!homes.containsKey(name)) {
            MessageUtils.sendConfigMessage(player, "messages.home.home-not-found", "Home {name} not found.", "name", homeName);
            return false;
        }
        
        // Delete the home
        homes.remove(name);
        
        // If the player has no more homes, remove them from the map
        if (homes.isEmpty()) {
            playerHomes.remove(uuid);
        }
        
        // Save the changes immediately
        saveAllHomes();
        
        MessageUtils.sendConfigMessage(player, "messages.home.home-deleted", "Home {name} has been deleted.", "name", homeName);
        return true;
    }
    
    /**
     * Get a home location for a player
     * 
     * @param player The player
     * @param homeName The name of the home
     * @return The home location, or null if not found
     */
    public Location getHome(Player player, String homeName) {
        if (player == null) return null;
        
        UUID uuid = player.getUniqueId();
        String name = homeName != null ? homeName.toLowerCase() : "home";
        
        // Check if the player has any homes
        if (!playerHomes.containsKey(uuid)) {
            return null;
        }
        
        Map<String, Location> homes = playerHomes.get(uuid);
        
        // If no home name was specified, try to get the default home
        if (homeName == null || homeName.isEmpty()) {
            // Try "home" first
            if (homes.containsKey("home")) {
                return homes.get("home");
            }
            
            // If "home" doesn't exist, return the first home
            if (!homes.isEmpty()) {
                return homes.values().iterator().next();
            }
            
            return null;
        }
        
        // Return the specified home
        return homes.get(name);
    }
    
    /**
     * Get all homes for a player
     * 
     * @param player The player
     * @return A map of home names to locations
     */
    public Map<String, Location> getHomes(Player player) {
        if (player == null) return Collections.emptyMap();
        
        UUID uuid = player.getUniqueId();
        
        // Return an empty map if the player has no homes
        if (!playerHomes.containsKey(uuid)) {
            return Collections.emptyMap();
        }
        
        // Return a copy of the player's homes map
        return new HashMap<>(playerHomes.get(uuid));
    }
    
    /**
     * Get the maximum number of homes a player can have
     * 
     * @param player The player
     * @return The maximum number of homes
     */
    public int getMaxHomes(Player player) {
        if (player == null) return defaultMaxHomes;
        
        // Check for permission-based limits
        int maxHomes = defaultMaxHomes;
        
        // Sort the permission values in descending order
        List<Integer> sortedPermissions = new ArrayList<>(maxHomesByPermission);
        Collections.sort(sortedPermissions, Collections.reverseOrder());
        
        // Check each permission in descending order
        for (int permValue : sortedPermissions) {
            if (player.hasPermission("frizzlenessentials.homes." + permValue)) {
                maxHomes = permValue;
                break;
            }
        }
        
        return maxHomes;
    }
    
    /**
     * Get the number of homes a player has
     * 
     * @param player The player
     * @return The number of homes
     */
    public int getHomeCount(Player player) {
        if (player == null) return 0;
        return getHomes(player).size();
    }
    
    /**
     * List all homes for a player
     * 
     * @param player The player
     * @return A formatted string of home names
     */
    public String listHomes(Player player) {
        if (player == null) return "";
        
        Map<String, Location> homes = getHomes(player);
        
        if (homes.isEmpty()) {
            return "";
        }
        
        // Sort home names alphabetically
        List<String> homeNames = new ArrayList<>(homes.keySet());
        Collections.sort(homeNames);
        
        return String.join(", ", homeNames);
    }
    
    /**
     * Check if a player can set a home
     * 
     * @param player The player to check
     * @param homeName The name of the home
     * @return True if the player can set the home, false otherwise
     */
    public boolean canSetHome(Player player, String homeName) {
        if (player == null || homeName == null) return false;
        
        // If the home already exists, they can overwrite it
        if (getHomes(player).containsKey(homeName.toLowerCase())) {
            return true;
        }
        
        // Check if they've reached their limit
        return getHomeCount(player) < getMaxHomes(player);
    }
} 