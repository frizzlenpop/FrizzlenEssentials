package com.frizzlenpop.frizzlenessentials.commands.fun;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LayCommand extends BaseCommand implements Listener {
    
    private static final String METADATA_KEY = "frizzlen_lay_bed";
    private final Map<UUID, ArmorStand> layingPlayers = new HashMap<>();
    private final boolean showParticles;
    private final boolean playSounds;
    
    public LayCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.lay", true);
        this.showParticles = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.show-particles", true);
        this.playSounds = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.play-sounds", true);
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if player is already laying
        if (isPlayerLaying(player)) {
            // Make the player stand up
            standUp(player);
            return true;
        }
        
        // Check if player is in a suitable location
        if (!canLayHere(player)) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-lay-here", "You cannot lay down here.");
            return true;
        }
        
        // Make the player lay down
        layDown(player);
        
        return true;
    }
    
    /**
     * Check if a player is laying down
     * 
     * @param player The player to check
     * @return True if the player is laying down, false otherwise
     */
    private boolean isPlayerLaying(Player player) {
        return layingPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * Make a player lay down
     * 
     * @param player The player to make lay down
     */
    private void layDown(Player player) {
        Location loc = player.getLocation();
        
        // Adjust the location for better visuals (flat on the ground)
        Location layLoc = loc.clone();
        layLoc.setY(layLoc.getY() - 0.6); // Lower to the ground
        
        // Set the armor stand's rotation to match the player's
        float yaw = loc.getYaw();
        layLoc.setYaw(yaw);
        
        // Create an invisible armor stand
        ArmorStand bed = (ArmorStand) player.getWorld().spawnEntity(layLoc, EntityType.ARMOR_STAND);
        bed.setVisible(false);
        bed.setGravity(false);
        bed.setMarker(true);
        bed.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        
        // Make the armor stand pose like a bed (flat)
        bed.setArms(false);
        bed.setBasePlate(false);
        
        // Configure the pose to make it flat
        EulerAngle angle = new EulerAngle(Math.toRadians(90), 0, 0);
        bed.setHeadPose(angle);
        bed.setBodyPose(angle);
        
        // Make the player ride the armor stand
        bed.addPassenger(player);
        
        // Register the player as laying
        layingPlayers.put(player.getUniqueId(), bed);
        
        // Play effects if enabled
        if (showParticles) {
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.1, 0.5, 0.05);
        }
        
        if (playSounds) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 0.8f);
        }
        
        // Send message
        MessageUtils.sendConfigMessage(player, "messages.fun.laying", "You are now laying down.");
    }
    
    /**
     * Make a player stand up
     * 
     * @param player The player to make stand up
     */
    private void standUp(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (layingPlayers.containsKey(uuid)) {
            // Get the bed
            ArmorStand bed = layingPlayers.get(uuid);
            
            if (bed != null && !bed.isDead()) {
                // Remove the player from the bed
                bed.eject();
                
                // Remove the bed
                bed.remove();
            }
            
            // Unregister the player
            layingPlayers.remove(uuid);
            
            // Teleport the player slightly up to prevent falling through blocks
            Location standLoc = player.getLocation().clone().add(0, 0.5, 0);
            player.teleport(standLoc);
            
            // Play effects if enabled
            if (showParticles) {
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.1, 0.5, 0.05);
            }
            
            if (playSounds) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.2f);
            }
        }
    }
    
    /**
     * Check if a player can lay at their current location
     * 
     * @param player The player to check
     * @return True if the player can lay, false otherwise
     */
    private boolean canLayHere(Player player) {
        Location loc = player.getLocation();
        
        // Check if there's a solid block below the player
        Block blockBelow = loc.clone().subtract(0, 1, 0).getBlock();
        if (blockBelow.getType() == Material.AIR || !blockBelow.getType().isSolid()) {
            return false;
        }
        
        // Check if there's enough room (two blocks of air above)
        Block blockAtHead = loc.clone().add(0, 1, 0).getBlock();
        Block blockAboveHead = loc.clone().add(0, 2, 0).getBlock();
        if (blockAtHead.getType() != Material.AIR || blockAboveHead.getType() != Material.AIR) {
            return false;
        }
        
        // Check for entities nearby to prevent laying inside other entities
        List<Entity> nearbyEntities = player.getNearbyEntities(1.0, 1.0, 1.0);
        for (Entity entity : nearbyEntities) {
            if (entity.getType() == EntityType.ARMOR_STAND && entity.hasMetadata(METADATA_KEY)) {
                return false;
            }
        }
        
        return true;
    }
    
    // Event handlers to manage laying players
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is laying
        if (!isPlayerLaying(player)) {
            return;
        }
        
        // Prevent players from controlling their movement while laying
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            // Player is trying to move horizontally while laying, cancel it
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        // Make player stand up when teleporting
        if (isPlayerLaying(player)) {
            standUp(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Make player stand up when quitting
        if (isPlayerLaying(player)) {
            standUp(player);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        
        // Check if entity is a player
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            // Make player stand up when damaged
            if (isPlayerLaying(player)) {
                standUp(player);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is laying and trying to interact
        if (isPlayerLaying(player) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // Make player stand up when interacting
            standUp(player);
        }
    }
    
    /**
     * Clean up when the plugin is disabled
     */
    public void cleanup() {
        // Make all laying players stand up
        for (UUID uuid : new ArrayList<>(layingPlayers.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                standUp(player);
            } else {
                ArmorStand bed = layingPlayers.get(uuid);
                if (bed != null && !bed.isDead()) {
                    bed.remove();
                }
                layingPlayers.remove(uuid);
            }
        }
        
        // Unregister the listener
        HandlerList.unregisterAll(this);
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 