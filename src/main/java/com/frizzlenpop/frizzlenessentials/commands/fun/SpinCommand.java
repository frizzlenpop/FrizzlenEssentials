package com.frizzlenpop.frizzlenessentials.commands.fun;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpinCommand extends BaseCommand implements Listener {
    
    private static final String METADATA_KEY = "frizzlen_spinning";
    private final Map<UUID, BukkitTask> spinningPlayers = new HashMap<>();
    private final boolean showParticles;
    private final boolean playSounds;
    private final double spinSpeed;
    
    public SpinCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.spin", true);
        this.showParticles = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.show-particles", true);
        this.playSounds = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.play-sounds", true);
        this.spinSpeed = plugin.getConfigManager().getMainConfig().getDouble("fun-commands.spin-speed", 15.0);
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if player is already spinning
        if (isPlayerSpinning(player)) {
            // Make the player stop spinning
            stopSpinning(player);
            return true;
        }
        
        // Start spinning the player
        startSpinning(player);
        
        return true;
    }
    
    /**
     * Check if a player is spinning
     * 
     * @param player The player to check
     * @return True if the player is spinning, false otherwise
     */
    private boolean isPlayerSpinning(Player player) {
        return player.hasMetadata(METADATA_KEY) || spinningPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * Make a player start spinning
     * 
     * @param player The player to make spin
     */
    private void startSpinning(Player player) {
        // Make sure player is not already spinning
        if (isPlayerSpinning(player)) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Mark the player as spinning
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));
        
        // Store the player's starting location
        final Location startLocation = player.getLocation().clone();
        
        // Create a task to spin the player
        BukkitTask task = new BukkitRunnable() {
            private float currentYaw = player.getLocation().getYaw();
            private int particleTicks = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !player.hasMetadata(METADATA_KEY)) {
                    stopSpinning(player);
                    cancel();
                    return;
                }
                
                // Get current location
                Location loc = player.getLocation();
                
                // Check if player has moved from starting position (teleport back if necessary)
                if (hasPlayerMoved(startLocation, loc)) {
                    Location newLoc = startLocation.clone();
                    newLoc.setYaw(loc.getYaw());
                    newLoc.setPitch(loc.getPitch());
                    player.teleport(newLoc);
                    loc = newLoc;
                }
                
                // Increment the yaw for spinning effect
                currentYaw += (float) spinSpeed;
                if (currentYaw >= 360.0f) {
                    currentYaw -= 360.0f;
                }
                
                // Apply the rotation
                Location newLoc = loc.clone();
                newLoc.setYaw(currentYaw);
                player.teleport(newLoc);
                
                // Create particles every few ticks for visual effect
                if (showParticles && ++particleTicks % 5 == 0) {
                    player.getWorld().spawnParticle(
                            Particle.CLOUD,
                            player.getLocation().add(0, 1, 0),
                            8, 0.5, 0.5, 0.5, 0.05
                    );
                }
                
                // Play sound occasionally
                if (playSounds && particleTicks % 20 == 0) {
                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
                            0.2f, 1.0f
                    );
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
        
        spinningPlayers.put(uuid, task);
        
        // Play initial sound effect
        if (playSounds) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.0f);
        }
        
        // Send message
        MessageUtils.sendConfigMessage(player, "messages.fun.spinning", "You are now spinning.");
    }
    
    /**
     * Make a player stop spinning
     * 
     * @param player The player to make stop spinning
     */
    private void stopSpinning(Player player) {
        if (player == null) return;
        
        UUID uuid = player.getUniqueId();
        
        // Remove the metadata
        if (player.hasMetadata(METADATA_KEY)) {
            player.removeMetadata(METADATA_KEY, plugin);
        }
        
        // Cancel the spinning task
        if (spinningPlayers.containsKey(uuid)) {
            BukkitTask task = spinningPlayers.remove(uuid);
            if (task != null) {
                task.cancel();
            }
        }
        
        // Play stop sound effect
        if (player.isOnline() && playSounds) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.5f);
            
            // Create final particles
            if (showParticles) {
                player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(0, 1, 0),
                        15, 0.5, 0.5, 0.5, 0.1
                );
            }
        }
    }
    
    /**
     * Check if a player has moved from their starting position
     * 
     * @param startLoc The starting location
     * @param currentLoc The current location
     * @return True if the player has moved significantly, false otherwise
     */
    private boolean hasPlayerMoved(Location startLoc, Location currentLoc) {
        // Check if the player has moved more than a small threshold
        return startLoc.getWorld() != currentLoc.getWorld() ||
               startLoc.distance(currentLoc) > 0.1 ||
               Math.abs(startLoc.getY() - currentLoc.getY()) > 0.1;
    }
    
    /**
     * Prevent players from moving while spinning
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is spinning
        if (!isPlayerSpinning(player)) {
            return;
        }
        
        // Allow rotation but prevent movement
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to != null && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
            // Player is trying to move, just update the yaw and pitch
            Location newLoc = from.clone();
            newLoc.setYaw(to.getYaw());
            newLoc.setPitch(to.getPitch());
            event.setTo(newLoc);
        }
    }
    
    /**
     * Stop spinning when player teleports
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is spinning and this isn't our own teleport
        if (isPlayerSpinning(player) && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            stopSpinning(player);
        }
    }
    
    /**
     * Stop spinning when player quits
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up when player quits
        if (isPlayerSpinning(player)) {
            stopSpinning(player);
        }
    }
    
    /**
     * Clean up when the plugin is disabled
     */
    public void cleanup() {
        // Stop all spinning players
        for (UUID uuid : new ArrayList<>(spinningPlayers.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                stopSpinning(player);
            } else {
                // Just cancel the task if the player is offline
                BukkitTask task = spinningPlayers.remove(uuid);
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