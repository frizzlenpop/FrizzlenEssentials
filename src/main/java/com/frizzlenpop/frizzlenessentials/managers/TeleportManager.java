package com.frizzlenpop.frizzlenessentials.managers;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {
    
    private final FrizzlenEssentials plugin;
    private final Map<UUID, UUID> teleportRequests;
    private final Map<UUID, Long> teleportRequestTimestamps;
    private final Map<UUID, Boolean> requestTypes; // true for tpahere, false for tpa
    private final Map<UUID, BukkitTask> pendingTeleports;
    private final Map<UUID, Location> lastLocations;
    private final Map<UUID, Location> deathLocations;
    private final Map<UUID, Long> teleportCooldowns;
    
    private final int teleportDelay;
    private final int teleportCooldown;
    private final int requestTimeout;
    private final boolean cancelOnMove;
    private final boolean saveLastLocation;
    private final int maxBackLocations;
    
    public TeleportManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        this.teleportRequests = new ConcurrentHashMap<>();
        this.teleportRequestTimestamps = new ConcurrentHashMap<>();
        this.requestTypes = new ConcurrentHashMap<>();
        this.pendingTeleports = new ConcurrentHashMap<>();
        this.lastLocations = new ConcurrentHashMap<>();
        this.deathLocations = new ConcurrentHashMap<>();
        this.teleportCooldowns = new ConcurrentHashMap<>();
        
        // Load settings from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.teleportDelay = config.getInt("teleportation.delay", 3);
        this.teleportCooldown = config.getInt("teleportation.cooldown", 3);
        this.requestTimeout = config.getInt("teleportation.request-timeout", 60);
        this.cancelOnMove = config.getBoolean("teleportation.cancel-on-move", true);
        this.saveLastLocation = config.getBoolean("teleportation.save-last-location", true);
        this.maxBackLocations = config.getInt("teleportation.max-back-locations", 5);
        
        // Load saved locations
        loadLocations();
    }
    
    /**
     * Load saved locations from config
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
                        deathLocations.put(uuid, location);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in death locations: " + uuidStr);
                }
            }
        }
        
        // Load last locations
        ConfigurationSection lastSection = locationsConfig.getConfigurationSection("last-locations");
        if (lastSection != null) {
            for (String uuidStr : lastSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Location location = deserializeLocation(lastSection.getConfigurationSection(uuidStr));
                    if (location != null) {
                        lastLocations.put(uuid, location);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in last locations: " + uuidStr);
                }
            }
        }
    }
    
    /**
     * Save all locations to config
     */
    public void saveAllLocations() {
        FileConfiguration locationsConfig = plugin.getConfigManager().getLocationsConfig();
        
        // Clear existing sections
        locationsConfig.set("death-locations", null);
        locationsConfig.set("last-locations", null);
        
        // Save death locations
        for (Map.Entry<UUID, Location> entry : deathLocations.entrySet()) {
            String path = "death-locations." + entry.getKey().toString();
            serializeLocation(locationsConfig, path, entry.getValue());
        }
        
        // Save last locations
        for (Map.Entry<UUID, Location> entry : lastLocations.entrySet()) {
            String path = "last-locations." + entry.getKey().toString();
            serializeLocation(locationsConfig, path, entry.getValue());
        }
        
        // Save the config
        plugin.getConfigManager().saveLocationsConfig();
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
     * Teleport a player to a location
     * 
     * @param player The player to teleport
     * @param location The location to teleport to
     * @return True if teleport was successful or scheduled, false otherwise
     */
    public boolean teleport(Player player, Location location) {
        return teleport(player, location, TeleportCause.PLUGIN);
    }
    
    /**
     * Teleport a player to a location with a specific cause
     * 
     * @param player The player to teleport
     * @param location The location to teleport to
     * @param cause The cause of the teleport
     * @return True if teleport was successful or scheduled, false otherwise
     */
    public boolean teleport(Player player, Location location, TeleportCause cause) {
        if (player == null || location == null || location.getWorld() == null) return false;
        
        // Check cooldown
        if (isOnCooldown(player.getUniqueId())) {
            long remainingTime = (teleportCooldowns.get(player.getUniqueId()) + (teleportCooldown * 1000) - System.currentTimeMillis()) / 1000;
            MessageUtils.sendConfigMessage(player, "messages.errors.cooldown", "You must wait {time} seconds before teleporting again.", "time", remainingTime);
            return false;
        }
        
        // Cancel any pending teleports
        cancelPendingTeleport(player.getUniqueId());
        
        // Save last location if enabled
        if (saveLastLocation) {
            lastLocations.put(player.getUniqueId(), player.getLocation());
        }
        
        // If delay is 0, teleport immediately
        if (teleportDelay <= 0) {
            return executeTeleport(player, location, cause);
        }
        
        // Otherwise, schedule the teleport
        MessageUtils.sendConfigMessage(player, "messages.teleport.teleporting", "Teleporting in {time} seconds...", "time", teleportDelay);
        
        final Location playerStartLocation = player.getLocation();
        
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Remove from pending teleports
            pendingTeleports.remove(player.getUniqueId());
            
            // Check if player moved and cancel if needed
            if (cancelOnMove && !isSameLocation(playerStartLocation, player.getLocation())) {
                MessageUtils.sendConfigMessage(player, "messages.errors.teleport-cancelled", "Teleport cancelled because you moved.");
                return;
            }
            
            // Execute the teleport
            executeTeleport(player, location, cause);
        }, teleportDelay * 20L);
        
        pendingTeleports.put(player.getUniqueId(), task);
        return true;
    }
    
    /**
     * Execute a teleport immediately
     * 
     * @param player The player to teleport
     * @param location The location to teleport to
     * @param cause The cause of the teleport
     * @return True if teleport was successful, false otherwise
     */
    private boolean executeTeleport(Player player, Location location, TeleportCause cause) {
        boolean success = player.teleport(location, cause);
        
        if (success) {
            // Set cooldown
            teleportCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
        
        return success;
    }
    
    /**
     * Check if a player is on teleport cooldown
     * 
     * @param uuid The UUID of the player
     * @return True if the player is on cooldown, false otherwise
     */
    public boolean isOnCooldown(UUID uuid) {
        if (teleportCooldown <= 0) return false;
        if (!teleportCooldowns.containsKey(uuid)) return false;
        
        long lastTeleport = teleportCooldowns.get(uuid);
        return System.currentTimeMillis() - lastTeleport < teleportCooldown * 1000;
    }
    
    /**
     * Cancel a pending teleport for a player
     * 
     * @param uuid The UUID of the player
     */
    public void cancelPendingTeleport(UUID uuid) {
        BukkitTask task = pendingTeleports.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Check if two locations are the same (for movement detection)
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     * @return True if the locations are the same, false otherwise
     */
    private boolean isSameLocation(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return false;
        if (loc1.getWorld() != loc2.getWorld()) return false;
        
        // Check if the player moved more than 0.1 blocks in any direction
        return Math.abs(loc1.getX() - loc2.getX()) < 0.1 &&
               Math.abs(loc1.getY() - loc2.getY()) < 0.1 &&
               Math.abs(loc1.getZ() - loc2.getZ()) < 0.1;
    }
    
    /**
     * Send a teleport request from one player to another
     * 
     * @param sender The player sending the request
     * @param target The player receiving the request
     * @param isHereRequest True if this is a "teleport here" request, false for a regular teleport request
     * @return True if the request was sent, false otherwise
     */
    public boolean sendTeleportRequest(Player sender, Player target, boolean isHereRequest) {
        if (sender == null || target == null) return false;
        
        UUID senderUuid = sender.getUniqueId();
        UUID targetUuid = target.getUniqueId();
        
        // Check if the target already has a pending request from this sender
        if (teleportRequests.containsKey(targetUuid) && teleportRequests.get(targetUuid).equals(senderUuid)) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.request-already-sent", "You already have a pending teleport request to this player.");
            return false;
        }
        
        // Store the request
        teleportRequests.put(targetUuid, senderUuid);
        teleportRequestTimestamps.put(targetUuid, System.currentTimeMillis());
        requestTypes.put(targetUuid, isHereRequest);
        
        // Send messages
        if (isHereRequest) {
            MessageUtils.sendConfigMessage(sender, "messages.teleport.teleport-here-request-sent", "Teleport here request sent to {player}.", "player", target.getName());
            MessageUtils.sendConfigMessage(target, "messages.teleport.teleport-here-request-received", "{player} wants you to teleport to them. Use /tpaccept or /tpdeny.", "player", sender.getName());
        } else {
            MessageUtils.sendConfigMessage(sender, "messages.teleport.teleport-request-sent", "Teleport request sent to {player}.", "player", target.getName());
            MessageUtils.sendConfigMessage(target, "messages.teleport.teleport-request-received", "{player} wants to teleport to you. Use /tpaccept or /tpdeny.", "player", sender.getName());
        }
        
        // Schedule request expiration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Check if the request is still pending and from the same sender
            if (teleportRequests.containsKey(targetUuid) && teleportRequests.get(targetUuid).equals(senderUuid)) {
                teleportRequests.remove(targetUuid);
                teleportRequestTimestamps.remove(targetUuid);
                requestTypes.remove(targetUuid);
                
                Player senderPlayer = Bukkit.getPlayer(senderUuid);
                Player targetPlayer = Bukkit.getPlayer(targetUuid);
                
                if (senderPlayer != null) {
                    MessageUtils.sendConfigMessage(senderPlayer, "messages.teleport.teleport-request-expired", "Your teleport request to {player} has expired.", "player", target.getName());
                }
                
                if (targetPlayer != null) {
                    MessageUtils.sendConfigMessage(targetPlayer, "messages.teleport.teleport-request-expired", "Teleport request from {player} has expired.", "player", sender.getName());
                }
            }
        }, requestTimeout * 20L);
        
        return true;
    }
    
    /**
     * Accept a pending teleport request
     * 
     * @param player The player accepting the request
     * @return True if a request was accepted, false otherwise
     */
    public boolean acceptTeleportRequest(Player player) {
        if (player == null) return false;
        
        UUID playerUuid = player.getUniqueId();
        
        // Check if there's a pending request
        if (!teleportRequests.containsKey(playerUuid)) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.no-pending-requests", "You have no pending teleport requests.");
            return false;
        }
        
        // Check if the request has expired
        long requestTime = teleportRequestTimestamps.getOrDefault(playerUuid, 0L);
        if (System.currentTimeMillis() - requestTime > requestTimeout * 1000) {
            teleportRequests.remove(playerUuid);
            teleportRequestTimestamps.remove(playerUuid);
            requestTypes.remove(playerUuid);
            MessageUtils.sendConfigMessage(player, "messages.teleport.teleport-request-expired", "The teleport request has expired.");
            return false;
        }
        
        // Get the sender
        UUID senderUuid = teleportRequests.get(playerUuid);
        Player sender = Bukkit.getPlayer(senderUuid);
        
        if (sender == null || !sender.isOnline()) {
            teleportRequests.remove(playerUuid);
            teleportRequestTimestamps.remove(playerUuid);
            requestTypes.remove(playerUuid);
            MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", "The player who sent the request is no longer online.");
            return false;
        }
        
        // Determine who teleports to whom
        boolean isHereRequest = requestTypes.getOrDefault(playerUuid, false);
        
        // Execute the teleport
        boolean success;
        if (isHereRequest) {
            // Target teleports to sender for tpahere
            success = teleport(player, sender.getLocation());
        } else {
            // Sender teleports to target for tpa
            success = teleport(sender, player.getLocation());
        }
        
        // Clean up the request
        teleportRequests.remove(playerUuid);
        teleportRequestTimestamps.remove(playerUuid);
        requestTypes.remove(playerUuid);
        
        // Send messages
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.teleport-accepted", "Teleport request accepted.");
            MessageUtils.sendConfigMessage(sender, "messages.teleport.teleport-accepted", "Your teleport request was accepted.");
        }
        
        return success;
    }
    
    /**
     * Deny a pending teleport request
     * 
     * @param player The player denying the request
     * @return True if a request was denied, false otherwise
     */
    public boolean denyTeleportRequest(Player player) {
        if (player == null) return false;
        
        UUID playerUuid = player.getUniqueId();
        
        // Check if there's a pending request
        if (!teleportRequests.containsKey(playerUuid)) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.no-pending-requests", "You have no pending teleport requests.");
            return false;
        }
        
        // Get the sender
        UUID senderUuid = teleportRequests.get(playerUuid);
        Player sender = Bukkit.getPlayer(senderUuid);
        
        // Clean up the request
        teleportRequests.remove(playerUuid);
        teleportRequestTimestamps.remove(playerUuid);
        requestTypes.remove(playerUuid);
        
        // Send messages
        MessageUtils.sendConfigMessage(player, "messages.teleport.teleport-denied", "Teleport request denied.");
        
        if (sender != null && sender.isOnline()) {
            MessageUtils.sendConfigMessage(sender, "messages.teleport.teleport-denied", "Your teleport request was denied by {player}.", "player", player.getName());
        }
        
        return true;
    }
    
    /**
     * Set a player's death location
     * 
     * @param uuid The UUID of the player
     * @param location The death location
     */
    public void setDeathLocation(UUID uuid, Location location) {
        if (uuid == null || location == null) return;
        deathLocations.put(uuid, location.clone());
    }
    
    /**
     * Get a player's death location
     * 
     * @param uuid The UUID of the player
     * @return The death location, or null if not found
     */
    public Location getDeathLocation(UUID uuid) {
        Location loc = deathLocations.get(uuid);
        return loc != null ? loc.clone() : null;
    }
    
    /**
     * Get a player's last location before teleporting
     * 
     * @param uuid The UUID of the player
     * @return The last location, or null if not found
     */
    public Location getLastLocation(UUID uuid) {
        Location loc = lastLocations.get(uuid);
        return loc != null ? loc.clone() : null;
    }
    
    /**
     * Teleport all online players to a location
     * 
     * @param location The location to teleport to
     * @param sender The player who initiated the teleport (for messaging)
     * @return The number of players teleported
     */
    public int teleportAll(Location location, Player sender) {
        if (location == null) return 0;
        
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Skip the sender
            if (player.equals(sender)) continue;
            
            if (teleport(player, location)) {
                count++;
                MessageUtils.sendConfigMessage(player, "messages.teleport.teleported-by-admin", "You have been teleported by {player}.", "player", sender.getName());
            }
        }
        
        return count;
    }
    
    /**
     * Teleport to a specific world's spawn location
     * 
     * @param player The player to teleport
     * @param worldName The name of the world to teleport to
     * @return True if teleport was successful, false otherwise
     */
    public boolean teleportToWorld(Player player, String worldName) {
        if (player == null || worldName == null) return false;
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.world-not-found", "World {world} not found.", "world", worldName);
            return false;
        }
        
        return teleport(player, world.getSpawnLocation());
    }
    
    /**
     * Teleport a player back to their previous location
     * 
     * @param player The player to teleport
     * @return True if teleport was successful, false otherwise
     */
    public boolean back(Player player) {
        if (player == null) return false;
        
        Location lastLocation = getLastLocation(player.getUniqueId());
        if (lastLocation == null) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.no-back-location", "No previous location found.");
            return false;
        }
        
        // Clear the last location before teleporting to prevent infinite loops
        UUID uuid = player.getUniqueId();
        Location currentLocation = player.getLocation().clone();
        lastLocations.remove(uuid);
        
        boolean success = teleport(player, lastLocation);
        
        // If the teleport fails, restore the last location
        if (!success) {
            lastLocations.put(uuid, currentLocation);
        }
        
        return success;
    }
    
    /**
     * Handle player death
     * 
     * @param player The player who died
     * @param location The death location
     */
    public void handleDeath(Player player, Location location) {
        if (player == null || location == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Save death location
        deathLocations.put(uuid, location.clone());
        
        // Save last location for /back if enabled
        if (saveLastLocation) {
            lastLocations.put(uuid, location.clone());
        }
    }
    
    /**
     * Check if a player has a pending teleport request
     * 
     * @param uuid The UUID of the player
     * @return True if the player has a pending request, false otherwise
     */
    public boolean hasPendingRequest(UUID uuid) {
        if (!teleportRequests.containsKey(uuid)) return false;
        
        // Check if the request has expired
        long requestTime = teleportRequestTimestamps.getOrDefault(uuid, 0L);
        if (System.currentTimeMillis() - requestTime > requestTimeout * 1000) {
            // Clean up expired request
            teleportRequests.remove(uuid);
            teleportRequestTimestamps.remove(uuid);
            requestTypes.remove(uuid);
            return false;
        }
        
        return true;
    }
} 