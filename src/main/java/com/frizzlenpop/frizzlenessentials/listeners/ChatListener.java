package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {
    
    private final FrizzlenEssentials plugin;
    
    public ChatListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // Get nickname from config
        String nickname = getNickname(player);
        
        // Apply nickname to chat format if set
        if (nickname != null && !nickname.isEmpty()) {
            String chatFormat = event.getFormat();
            
            // Replace the player's name with their nickname in the chat format
            // This preserves any other formatting from other plugins
            chatFormat = chatFormat.replaceFirst(
                    "\\b" + player.getName() + "\\b", 
                    nickname);
            
            event.setFormat(chatFormat);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Get nickname from config
        String nickname = getNickname(player);
        
        // Apply nickname
        if (nickname != null && !nickname.isEmpty()) {
            player.setDisplayName(nickname);
        }
    }
    
    /**
     * Gets the nickname for a player
     * 
     * @param player The player
     * @return The nickname or null if not set
     */
    private String getNickname(Player player) {
        if (player == null) return null;
        
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();
        String uuid = player.getUniqueId().toString();
        
        return playersConfig.getString("players." + uuid + ".nickname");
    }
} 