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

public class WarpManager {
    
    private final FrizzlenEssentials plugin;
    private final Map<String, Location> warps;
    private final Map<String, UUID> warpCreators;
    private final Map<String, Set<UUID>> warpAccess;
    private final boolean publicByDefault;
    private final boolean showInaccessibleWarps;
    
    public WarpManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        this.warps = new ConcurrentHashMap<>();
        this.warpCreators = new ConcurrentHashMap<>();
        this.warpAccess = new ConcurrentHashMap<>();
        
        // Load settings from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.publicByDefault = config.getBoolean("warps.public-by-default", true);
        this.showInaccessibleWarps = config.getBoolean("warps.show-inaccessible-warps", false);
        
        // Load warps from config
        loadWarps();
    }
    
    /**
     * Load warps from config
     */
    private void loadWarps() {
        FileConfiguration warpsConfig = plugin.getConfigManager().getWarpsConfig();
        
        ConfigurationSection warpsSection = warpsConfig.getConfigurationSection("warps");
        if (warpsSection == null) return;
        
        for (String warpName : warpsSection.getKeys(false)) {
            ConfigurationSection warpSection = warpsSection.getConfigurationSection(warpName);
            
            if (warpSection != null) {
                // Load location
                Location location = deserializeLocation(warpSection);
                
                if (location != null) {
                    String name = warpName.toLowerCase();
                    warps.put(name, location);
                    
                    // Load creator
                    String creatorUuidStr = warpSection.getString("creator");
                    if (creatorUuidStr != null) {
                        try {
                            UUID creatorUuid = UUID.fromString(creatorUuidStr);
                            warpCreators.put(name, creatorUuid);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid creator UUID for warp: " + warpName);
                        }
                    }
                    
                    // Load access list
                    List<String> accessList = warpSection.getStringList("access");
                    if (!accessList.isEmpty()) {
                        Set<UUID> accessSet = new HashSet<>();
                        
                        for (String uuidStr : accessList) {
                            try {
                                UUID uuid = UUID.fromString(uuidStr);
                                accessSet.add(uuid);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Invalid UUID in access list for warp: " + warpName);
                            }
                        }
                        
                        if (!accessSet.isEmpty()) {
                            warpAccess.put(name, accessSet);
                        }
                    }
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + warps.size() + " warps.");
    }
    
    /**
     * Save all warps to config
     */
    public void saveAllWarps() {
        FileConfiguration warpsConfig = plugin.getConfigManager().getWarpsConfig();
        
        // Clear existing warps
        warpsConfig.set("warps", null);
        
        // Save all warps
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String warpName = entry.getKey();
            Location location = entry.getValue();
            
            String path = "warps." + warpName;
            serializeLocation(warpsConfig, path, location);
            
            // Save creator
            UUID creatorUuid = warpCreators.get(warpName);
            if (creatorUuid != null) {
                warpsConfig.set(path + ".creator", creatorUuid.toString());
            }
            
            // Save access list
            Set<UUID> accessSet = warpAccess.get(warpName);
            if (accessSet != null && !accessSet.isEmpty()) {
                List<String> accessList = new ArrayList<>();
                for (UUID uuid : accessSet) {
                    accessList.add(uuid.toString());
                }
                warpsConfig.set(path + ".access", accessList);
            }
        }
        
        // Save the config
        plugin.getConfigManager().saveWarpsConfig();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        plugin.getLogger().info("Saved " + warps.size() + " warps.");
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
     * Create a warp
     * 
     * @param player The player creating the warp
     * @param warpName The name of the warp
     * @param location The location of the warp
     * @return True if the warp was created, false otherwise
     */
    public boolean createWarp(Player player, String warpName, Location location) {
        if (player == null || warpName == null || location == null) return false;
        
        String name = warpName.toLowerCase();
        
        // Check if the warp already exists
        if (warps.containsKey(name)) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-exists", "A warp with this name already exists.");
            return false;
        }
        
        // Create the warp
        warps.put(name, location);
        warpCreators.put(name, player.getUniqueId());
        
        // If not public by default, add the creator to the access list
        if (!publicByDefault) {
            Set<UUID> accessSet = new HashSet<>();
            accessSet.add(player.getUniqueId());
            warpAccess.put(name, accessSet);
        }
        
        // Save warps to ensure persistence
        saveAllWarps();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        MessageUtils.sendConfigMessage(player, "messages.warp.warp-set", "Warp {name} has been created.", "name", warpName);
        return true;
    }
    
    /**
     * Delete a warp
     * 
     * @param player The player deleting the warp
     * @param warpName The name of the warp
     * @return True if the warp was deleted, false otherwise
     */
    public boolean deleteWarp(Player player, String warpName) {
        if (player == null || warpName == null) return false;
        
        String name = warpName.toLowerCase();
        
        // Check if the warp exists
        if (!warps.containsKey(name)) {
            MessageUtils.sendConfigMessage(player, "messages.warp.warp-not-found", "Warp {name} not found.", "name", warpName);
            return false;
        }
        
        // Check if the player has permission to delete the warp
        if (!player.hasPermission("frizzlenessentials.delwarp.others")) {
            UUID creatorUuid = warpCreators.get(name);
            if (creatorUuid != null && !creatorUuid.equals(player.getUniqueId())) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", "You don't have permission to delete this warp.");
                return false;
            }
        }
        
        // Delete the warp
        warps.remove(name);
        warpCreators.remove(name);
        warpAccess.remove(name);
        
        // Save warps to ensure persistence
        saveAllWarps();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        MessageUtils.sendConfigMessage(player, "messages.warp.warp-deleted", "Warp {name} has been deleted.", "name", warpName);
        return true;
    }
    
    /**
     * Get a warp location
     * 
     * @param warpName The name of the warp
     * @return The warp location, or null if not found
     */
    public Location getWarp(String warpName) {
        if (warpName == null) return null;
        
        String name = warpName.toLowerCase();
        return warps.get(name);
    }
    
    /**
     * Check if a player has access to a warp
     * 
     * @param player The player
     * @param warpName The name of the warp
     * @return True if the player has access, false otherwise
     */
    public boolean hasAccess(Player player, String warpName) {
        if (player == null || warpName == null) return false;
        
        String name = warpName.toLowerCase();
        
        // Check if the warp exists
        if (!warps.containsKey(name)) {
            return false;
        }
        
        // Admin override
        if (player.hasPermission("frizzlenessentials.warp.others")) {
            return true;
        }
        
        // Check if the warp is public
        if (!warpAccess.containsKey(name)) {
            return publicByDefault;
        }
        
        // Check if the player is in the access list
        Set<UUID> accessSet = warpAccess.get(name);
        return accessSet.contains(player.getUniqueId());
    }
    
    /**
     * Modify access to a warp for a player
     * 
     * @param admin The admin modifying access
     * @param warpName The name of the warp
     * @param targetPlayer The player to modify access for
     * @param grant True to grant access, false to revoke
     * @return True if access was modified, false otherwise
     */
    public boolean modifyAccess(Player admin, String warpName, Player targetPlayer, boolean grant) {
        if (admin == null || warpName == null || targetPlayer == null) return false;
        
        String name = warpName.toLowerCase();
        
        // Check if the warp exists
        if (!warps.containsKey(name)) {
            MessageUtils.sendConfigMessage(admin, "messages.warp.warp-not-found", "Warp {name} not found.", "name", warpName);
            return false;
        }
        
        // Check if the admin has permission to modify access
        if (!admin.hasPermission("frizzlenessentials.warpaccess")) {
            UUID creatorUuid = warpCreators.get(name);
            if (creatorUuid != null && !creatorUuid.equals(admin.getUniqueId())) {
                MessageUtils.sendConfigMessage(admin, "messages.errors.no-permission", "You don't have permission to modify access to this warp.");
                return false;
            }
        }
        
        // Get or create the access set
        Set<UUID> accessSet = warpAccess.computeIfAbsent(name, k -> new HashSet<>());
        
        // Modify access
        if (grant) {
            accessSet.add(targetPlayer.getUniqueId());
        } else {
            accessSet.remove(targetPlayer.getUniqueId());
        }
        
        // If the access set is empty and public by default, remove it
        if (accessSet.isEmpty() && publicByDefault) {
            warpAccess.remove(name);
        }
        
        // Save warps to ensure persistence
        saveAllWarps();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
        
        MessageUtils.sendConfigMessage(admin, "messages.warp.warp-access-modified", "Access for warp {name} has been modified for player {player}.", 
                "name", warpName, "player", targetPlayer.getName());
        return true;
    }
    
    /**
     * Get information about a warp
     * 
     * @param warpName The name of the warp
     * @return A map of information about the warp
     */
    public Map<String, Object> getWarpInfo(String warpName) {
        if (warpName == null) return Collections.emptyMap();
        
        String name = warpName.toLowerCase();
        
        // Check if the warp exists
        if (!warps.containsKey(name)) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("name", name);
        
        // Location
        Location location = warps.get(name);
        info.put("location", location);
        info.put("world", location.getWorld().getName());
        info.put("x", location.getX());
        info.put("y", location.getY());
        info.put("z", location.getZ());
        
        // Creator
        UUID creatorUuid = warpCreators.get(name);
        if (creatorUuid != null) {
            String creatorName = Bukkit.getOfflinePlayer(creatorUuid).getName();
            info.put("creator", creatorName != null ? creatorName : creatorUuid.toString());
        }
        
        // Access
        boolean isPublic = !warpAccess.containsKey(name) && publicByDefault;
        info.put("public", isPublic);
        
        if (!isPublic) {
            Set<UUID> accessSet = warpAccess.get(name);
            if (accessSet != null) {
                List<String> accessNames = new ArrayList<>();
                for (UUID uuid : accessSet) {
                    String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                    if (playerName != null) {
                        accessNames.add(playerName);
                    }
                }
                info.put("access", accessNames);
            }
        }
        
        return info;
    }
    
    /**
     * Get a list of all warps
     * 
     * @return A list of warp names
     */
    public List<String> getWarpList() {
        List<String> warpList = new ArrayList<>(warps.keySet());
        Collections.sort(warpList);
        return warpList;
    }
    
    /**
     * Get a list of warps accessible by a player
     * 
     * @param player The player
     * @return A list of accessible warp names
     */
    public List<String> getAccessibleWarps(Player player) {
        if (player == null) return Collections.emptyList();
        
        List<String> accessibleWarps = new ArrayList<>();
        
        for (String warpName : warps.keySet()) {
            if (hasAccess(player, warpName) || (showInaccessibleWarps && player.hasPermission("frizzlenessentials.warp"))) {
                accessibleWarps.add(warpName);
            }
        }
        
        Collections.sort(accessibleWarps);
        return accessibleWarps;
    }
} 