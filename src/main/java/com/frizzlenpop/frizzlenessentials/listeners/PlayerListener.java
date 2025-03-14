package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerListener implements Listener {
    
    private final FrizzlenEssentials plugin;
    
    public PlayerListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player should be teleported to spawn
        if (plugin.getSpawnManager().shouldTeleportOnJoin()) {
            plugin.getSpawnManager().teleportToSpawn(player);
        }
        
        // Load player data
        loadPlayerData(player);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Save player data
        savePlayerData(player);
    }
    
    /**
     * Load player data from config
     * 
     * @param player The player to load data for
     */
    private void loadPlayerData(Player player) {
        if (player == null) return;
        
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        // Check if player has persistent flight mode
        if (playersConfig.getBoolean("players." + uuid + ".flight", false) && 
                player.hasPermission("frizzlenessentials.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        
        // Check if player has persistent god mode
        if (playersConfig.getBoolean("players." + uuid + ".godmode", false) && 
                player.hasPermission("frizzlenessentials.god")) {
            // Set god mode (will be implemented in a separate manager)
            // For now, just store the state
            playersConfig.set("players." + uuid + ".godmode", true);
        }
        
        // Apply nickname if set
        String nickname = playersConfig.getString("players." + uuid + ".nickname");
        if (nickname != null && !nickname.isEmpty() && player.hasPermission("frizzlenessentials.nick")) {
            player.setDisplayName(nickname);
        }
    }
    
    /**
     * Save player data to config
     * 
     * @param player The player to save data for
     */
    private void savePlayerData(Player player) {
        if (player == null) return;
        
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        // Save flight mode if persistent
        if (plugin.getConfigManager().getMainConfig().getBoolean("player-utilities.persistent-flight", false)) {
            playersConfig.set("players." + uuid + ".flight", player.getAllowFlight());
        }
        
        // Save god mode if persistent
        if (plugin.getConfigManager().getMainConfig().getBoolean("player-utilities.persistent-godmode", false)) {
            // Get god mode state (will be implemented in a separate manager)
            // For now, just get the stored state
            boolean godMode = playersConfig.getBoolean("players." + uuid + ".godmode", false);
            playersConfig.set("players." + uuid + ".godmode", godMode);
        }
        
        // Save nickname if different from player name
        if (!player.getDisplayName().equals(player.getName())) {
            playersConfig.set("players." + uuid + ".nickname", player.getDisplayName());
        }
        
        // Save the config
        plugin.getConfigManager().savePlayersConfig();
        
        // Also save all configs to ensure changes are written to disk
        plugin.getConfigManager().saveConfigs();
    }
} 