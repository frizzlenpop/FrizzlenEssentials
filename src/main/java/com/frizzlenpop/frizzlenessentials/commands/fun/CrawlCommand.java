package com.frizzlenpop.frizzlenessentials.commands.fun;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrawlCommand extends BaseCommand implements Listener {
    
    private static final String METADATA_KEY = "frizzlen_crawling";
    private final Map<UUID, FallingBlock> crawlingPlayers = new HashMap<>();
    private final Map<UUID, BukkitTask> crawlTasks = new HashMap<>();
    private final boolean showParticles;
    private final boolean playSounds;
    
    public CrawlCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.crawl", true);
        this.showParticles = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.show-particles", true);
        this.playSounds = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.play-sounds", true);
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if player is already crawling
        if (isPlayerCrawling(player)) {
            // Make the player stop crawling
            stopCrawling(player);
            return true;
        }
        
        // Check if player can crawl here
        if (!canCrawlHere(player)) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-crawl-here", "You cannot crawl here.");
            return true;
        }
        
        // Make the player crawl
        startCrawling(player);
        
        return true;
    }
    
    /**
     * Check if a player is crawling
     * 
     * @param player The player to check
     * @return True if the player is crawling, false otherwise
     */
    private boolean isPlayerCrawling(Player player) {
        return player.hasMetadata(METADATA_KEY) || crawlingPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * Make a player start crawling
     * 
     * @param player The player to make crawl
     */
    private void startCrawling(Player player) {
        // Make sure player is not already crawling
        if (isPlayerCrawling(player)) {
            return;
        }
        
        // Check for valid gamemode
        if (player.getGameMode() == GameMode.SPECTATOR) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-crawl-in-spectator", "You cannot crawl in spectator mode.");
            return;
        }
        
        UUID uuid = player.getUniqueId();
        Location loc = player.getLocation();
        
        // Mark the player as crawling
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));
        
        // Method 1: Use a falling block to force the player into crawling state
        if (canUseBarrier(player)) {
            // Create temporary barrier block above player to force swimming animation
            FallingBlock barrier = player.getWorld().spawnFallingBlock(
                    loc.clone().add(0, 1.5, 0),
                    Material.BARRIER.createBlockData()
            );
            
            barrier.setGravity(false);
            barrier.setInvulnerable(true);
            barrier.setDropItem(false);
            barrier.setHurtEntities(false);
            barrier.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            
            // Store the barrier block
            crawlingPlayers.put(uuid, barrier);
            
            // Create a task to keep the barrier above the player
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || !player.hasMetadata(METADATA_KEY)) {
                        stopCrawling(player);
                        cancel();
                        return;
                    }
                    
                    // Keep the barrier above the player
                    Location playerLoc = player.getLocation();
                    barrier.teleport(playerLoc.clone().add(0, 1.5, 0));
                    
                    // Create particles for visual effect
                    if (showParticles) {
                        playerLoc.getWorld().spawnParticle(
                                Particle.DUST_COLOR_TRANSITION,
                                playerLoc.clone().add(0, 0.2, 0),
                                3, 0.2, 0.1, 0.2, 0.01
                        );
                    }
                }
            }.runTaskTimer(plugin, 1L, 5L);
            
            crawlTasks.put(uuid, task);
        } else {
            // Method 2: Use swimming effect (not as reliable but works in more situations)
            player.setSwimming(true);
            
            // Apply slow effect to simulate crawling
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 2, false, false, false));
            
            // Create a task to keep the player swimming
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || !player.hasMetadata(METADATA_KEY)) {
                        stopCrawling(player);
                        cancel();
                        return;
                    }
                    
                    // Force swimming state
                    player.setSwimming(true);
                    
                    // Create particles for visual effect
                    if (showParticles) {
                        Location playerLoc = player.getLocation();
                        playerLoc.getWorld().spawnParticle(
                                Particle.DUST_COLOR_TRANSITION,
                                playerLoc.clone().add(0, 0.2, 0),
                                3, 0.2, 0.1, 0.2, 0.01
                        );
                    }
                }
            }.runTaskTimer(plugin, 1L, 5L);
            
            crawlTasks.put(uuid, task);
        }
        
        // Play sound effect
        if (playSounds) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 0.5f, 0.7f);
        }
        
        // Send message
        MessageUtils.sendConfigMessage(player, "messages.fun.crawling", "You are now crawling.");
    }
    
    /**
     * Make a player stop crawling
     * 
     * @param player The player to make stop crawling
     */
    private void stopCrawling(Player player) {
        if (player == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Remove the metadata
        if (player.hasMetadata(METADATA_KEY)) {
            player.removeMetadata(METADATA_KEY, plugin);
        }
        
        // Remove the barrier block
        if (crawlingPlayers.containsKey(uuid)) {
            FallingBlock barrier = crawlingPlayers.remove(uuid);
            if (barrier != null && !barrier.isDead()) {
                barrier.remove();
            }
        }
        
        // Cancel the task
        if (crawlTasks.containsKey(uuid)) {
            BukkitTask task = crawlTasks.remove(uuid);
            if (task != null) {
                task.cancel();
            }
        }
        
        // Remove swimming and slow effect
        if (player.isOnline()) {
            player.setSwimming(false);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            
            // Play sound effect
            if (playSounds) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 0.5f, 1.2f);
            }
        }
    }
    
    /**
     * Check if a player can crawl at their current location
     * 
     * @param player The player to check
     * @return True if the player can crawl, false otherwise
     */
    private boolean canCrawlHere(Player player) {
        // Check for valid gamemode
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }
        
        // Check if there's a block below the player
        Location loc = player.getLocation();
        Block blockBelow = loc.getBlock().getRelative(BlockFace.DOWN);
        
        return blockBelow.getType().isSolid();
    }
    
    /**
     * Check if we can use barrier method for crawling
     * 
     * @param player The player to check
     * @return True if barrier method can be used, false otherwise
     */
    private boolean canUseBarrier(Player player) {
        // Check if there's enough space above
        Location loc = player.getLocation();
        Block blockAbove = loc.getBlock().getRelative(BlockFace.UP, 2);
        
        return blockAbove.getType() == Material.AIR;
    }
    
    // Event handlers
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        // If player starts sneaking while crawling, stop crawling
        if (event.isSneaking() && isPlayerCrawling(player)) {
            stopCrawling(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up when player quits
        if (isPlayerCrawling(player)) {
            stopCrawling(player);
        }
    }
    
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        
        // Stop crawling if player switches to spectator mode
        if (event.getNewGameMode() == GameMode.SPECTATOR && isPlayerCrawling(player)) {
            stopCrawling(player);
        }
    }
    
    /**
     * Clean up when the plugin is disabled
     */
    public void cleanup() {
        // Make all crawling players stop crawling
        for (UUID uuid : new ArrayList<>(crawlingPlayers.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                stopCrawling(player);
            } else {
                // Player is offline, just remove the barrier
                FallingBlock barrier = crawlingPlayers.remove(uuid);
                if (barrier != null && !barrier.isDead()) {
                    barrier.remove();
                }
                
                // Cancel the task
                BukkitTask task = crawlTasks.remove(uuid);
                if (task != null) {
                    task.cancel();
                }
            }
        }
        
        // Unregister listener
        HandlerList.unregisterAll(this);
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 