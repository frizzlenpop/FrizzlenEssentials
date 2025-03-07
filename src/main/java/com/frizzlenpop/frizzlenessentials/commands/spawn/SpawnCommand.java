package com.frizzlenpop.frizzlenessentials.commands.spawn;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand extends BaseCommand {
    
    public SpawnCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.spawn", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if spawn is set
        if (!plugin.getSpawnManager().isSpawnSet()) {
            MessageUtils.sendConfigMessage(player, "messages.spawn.not-set", "Spawn has not been set.");
            return true;
        }
        
        // Get the spawn location
        Location spawnLocation = plugin.getSpawnManager().getSpawn();
        
        // Teleport the player to spawn
        boolean success = plugin.getTeleportManager().teleport(player, spawnLocation);
        
        if (success) {
            MessageUtils.sendConfigMessage(player, "messages.spawn.teleport-to-spawn", "Teleporting to spawn.");
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 