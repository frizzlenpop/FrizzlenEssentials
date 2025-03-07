package com.frizzlenpop.frizzlenessentials.commands.fun;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SitCommand extends BaseCommand implements Listener {
    
    private static final String METADATA_KEY = "frizzlen_sit_chair";
    private final Map<UUID, ArmorStand> sittingPlayers = new HashMap<>();
    private final boolean showParticles;
    private final boolean playSounds;
    
    public SitCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.sit", true);
        this.showParticles = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.show-particles", true);
        this.playSounds = plugin.getConfigManager().getMainConfig().getBoolean("fun-commands.play-sounds", true);
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if player is already sitting
        if (isPlayerSitting(player)) {
            // Stand up the player
            standUp(player);
            return true;
        }
        
        // Check if player is in a suitable location
        if (!canSitHere(player)) {
            MessageUtils.sendConfigMessage(player, "messages.errors.cannot-sit-here", "You cannot sit here.");
            return true;
        }
        
        // Make the player sit
        sit(player);
        
        return true;
    }
    
    /**
     * Check if a player is sitting
     * 
     * @param player The player to check
     * @return True if the player is sitting, false otherwise
     */
    private boolean isPlayerSitting(Player player) {
        return sittingPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * Make a player sit
     * 
     * @param player The player to make sit
     */
    private void sit(Player player) {
        Location loc = player.getLocation();
        
        // Adjust the location slightly for better visuals
        Location seatLoc = loc.clone();
        seatLoc.setY(seatLoc.getY() - 0.5);
        
        // Create an invisible armor stand
        ArmorStand seat = (ArmorStand) player.getWorld().spawnEntity(seatLoc, EntityType.ARMOR_STAND);
        seat.setVisible(false);
        seat.setGravity(false);
        seat.setSmall(true);
        seat.setMarker(true);
        seat.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        
        // Make the player sit on the armor stand
        seat.addPassenger(player);
        
        // Register the player as sitting
        sittingPlayers.put(player.getUniqueId(), seat);
        
        // Play effects if enabled
        if (playSounds) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.0f);
        }
        
        // Send message
        MessageUtils.sendConfigMessage(player, "messages.fun.sitting", "You are now sitting.");
    }
    
    /**
     * Make a player stand up
     * 
     * @param player The player to make stand up
     */
    private void standUp(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (sittingPlayers.containsKey(uuid)) {
            // Get the seat
            ArmorStand seat = sittingPlayers.get(uuid);
            
            if (seat != null && !seat.isDead()) {
                // Remove the player from the seat
                seat.eject();
                
                // Remove the seat
                seat.remove();
            }
            
            // Unregister the player
            sittingPlayers.remove(uuid);
            
            // Play effects if enabled
            if (playSounds) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.0f);
            }
        }
    }
    
    /**
     * Check if a player can sit at their current location
     * 
     * @param player The player to check
     * @return True if the player can sit, false otherwise
     */
    private boolean canSitHere(Player player) {
        Location loc = player.getLocation();
        
        // Check if there's a block below the player
        Block blockBelow = loc.clone().subtract(0, 1, 0).getBlock();
        if (blockBelow.getType() == Material.AIR) {
            return false;
        }
        
        // Check if there's enough room (no block at player's head height)
        Block blockAtHead = loc.clone().add(0, 1, 0).getBlock();
        if (blockAtHead.getType() != Material.AIR) {
            return false;
        }
        
        // Check for entities nearby to prevent sitting inside other entities
        List<Entity> nearbyEntities = player.getNearbyEntities(0.5, 0.5, 0.5);
        for (Entity entity : nearbyEntities) {
            if (entity.getType() == EntityType.ARMOR_STAND && entity.hasMetadata(METADATA_KEY)) {
                return false;
            }
        }
        
        return true;
    }
    
    // Event handlers to manage sitting players
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is sitting
        if (!isPlayerSitting(player)) {
            return;
        }
        
        // Prevent players from controlling their movement while sitting
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            // Player is trying to move horizontally while sitting, cancel it
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        // Make player stand up when teleporting
        if (isPlayerSitting(player)) {
            standUp(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Make player stand up when quitting
        if (isPlayerSitting(player)) {
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
            if (isPlayerSitting(player)) {
                standUp(player);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is sitting and trying to interact
        if (isPlayerSitting(player) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // Make player stand up when interacting
            standUp(player);
        }
    }
    
    /**
     * Clean up when the plugin is disabled
     */
    public void cleanup() {
        // Make all sitting players stand up
        for (UUID uuid : new ArrayList<>(sittingPlayers.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                standUp(player);
            } else {
                ArmorStand seat = sittingPlayers.get(uuid);
                if (seat != null && !seat.isDead()) {
                    seat.remove();
                }
                sittingPlayers.remove(uuid);
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