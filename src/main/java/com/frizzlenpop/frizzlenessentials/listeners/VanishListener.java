package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class VanishListener implements Listener {
    
    private static final String VANISH_METADATA = "vanished";
    private final FrizzlenEssentials plugin;
    
    public VanishListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player was vanished before
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        if (playersConfig.getBoolean("players." + uuid + ".vanished", false)) {
            // Re-apply vanish
            player.setMetadata(VANISH_METADATA, new FixedMetadataValue(plugin, true));
            
            // Hide player from everyone without permission to see vanished players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("frizzlenessentials.vanish.see")) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }
        }
        
        // Hide vanished players from this player if they don't have permission
        if (!player.hasPermission("frizzlenessentials.vanish.see")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasMetadata(VANISH_METADATA)) {
                    player.hidePlayer(plugin, onlinePlayer);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Save vanish state if persistent
        if (plugin.getConfigManager().getMainConfig().getBoolean("player-utilities.persistent-vanish", false)) {
            FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
            String uuid = player.getUniqueId().toString();
            
            playersConfig.set("players." + uuid + ".vanished", player.hasMetadata(VANISH_METADATA));
            plugin.getConfigManager().savePlayersConfig();
        }
    }
} 