package com.frizzlenpop.frizzlenessentials.commands.spawn;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnCommand extends BaseCommand {
    
    public SetSpawnCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.setspawn", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Set the spawn location to the player's current location
        plugin.getSpawnManager().setSpawn(player.getLocation());
        
        // Also set the world's spawn point
        player.getWorld().setSpawnLocation(player.getLocation());
        
        // Make sure the spawn point is saved to disk
        plugin.getSpawnManager().saveSpawn();
        plugin.getConfigManager().saveConfigs();
        
        // Send a success message
        MessageUtils.sendConfigMessage(player, "messages.spawn.spawn-set", "Server spawn location has been set.");
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 