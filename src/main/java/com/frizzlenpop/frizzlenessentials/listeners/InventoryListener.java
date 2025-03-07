package com.frizzlenpop.frizzlenessentials.listeners;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
    
    private final FrizzlenEssentials plugin;
    
    public InventoryListener(FrizzlenEssentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Inventory inventory = event.getInventory();
        String title = event.getView().getTitle();
        
        // Handle InvSee inventory
        if (title.endsWith("'s Inventory")) {
            handleInvSeeClick(event);
        }
        // Handle EnderSee inventory
        else if (title.endsWith("'s Ender Chest")) {
            handleEnderSeeClick(event);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        
        // Handle InvSee inventory
        if (title.endsWith("'s Inventory")) {
            handleInvSeeDrag(event);
        }
        // Handle EnderSee inventory
        else if (title.endsWith("'s Ender Chest")) {
            handleEnderSeeDrag(event);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        
        // Handle InvSee inventory
        if (title.endsWith("'s Inventory")) {
            handleInvSeeClose(event);
        }
        // Handle EnderSee inventory
        else if (title.endsWith("'s Ender Chest")) {
            handleEnderSeeClose(event);
        }
    }
    
    private void handleInvSeeClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        Player viewer = (Player) event.getWhoClicked();
        
        // Check if viewer has permission to modify inventory
        if (!viewer.hasPermission("frizzlenessentials.invsee.modify")) {
            event.setCancelled(true);
            return;
        }
        
        // Update target's inventory
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getInventory().setContents(event.getInventory().getContents());
            target.updateInventory();
        });
    }
    
    private void handleEnderSeeClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        Player viewer = (Player) event.getWhoClicked();
        
        // Check if viewer has permission to modify ender chest
        if (!viewer.hasPermission("frizzlenessentials.endersee.modify")) {
            event.setCancelled(true);
            return;
        }
        
        // Update target's ender chest
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getEnderChest().setContents(event.getInventory().getContents());
        });
    }
    
    private void handleInvSeeDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        Player viewer = (Player) event.getWhoClicked();
        
        // Check if viewer has permission to modify inventory
        if (!viewer.hasPermission("frizzlenessentials.invsee.modify")) {
            event.setCancelled(true);
            return;
        }
        
        // Update target's inventory
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getInventory().setContents(event.getInventory().getContents());
            target.updateInventory();
        });
    }
    
    private void handleEnderSeeDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        Player viewer = (Player) event.getWhoClicked();
        
        // Check if viewer has permission to modify ender chest
        if (!viewer.hasPermission("frizzlenessentials.endersee.modify")) {
            event.setCancelled(true);
            return;
        }
        
        // Update target's ender chest
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getEnderChest().setContents(event.getInventory().getContents());
        });
    }
    
    private void handleInvSeeClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        
        // Update target's inventory one final time
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getInventory().setContents(event.getInventory().getContents());
            target.updateInventory();
        });
    }
    
    private void handleEnderSeeClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player)) return;
        
        Player target = (Player) holder;
        
        // Update target's ender chest one final time
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            target.getEnderChest().setContents(event.getInventory().getContents());
        });
    }
} 