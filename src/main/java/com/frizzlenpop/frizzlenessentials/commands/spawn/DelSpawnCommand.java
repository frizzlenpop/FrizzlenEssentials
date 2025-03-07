package com.frizzlenpop.frizzlenessentials.commands.spawn;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelSpawnCommand extends BaseCommand {
    
    public DelSpawnCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.delspawn", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if spawn is set
        if (!plugin.getSpawnManager().isSpawnSet()) {
            MessageUtils.sendConfigMessage(player, "messages.spawn.not-set", "Spawn has not been set.");
            return true;
        }
        
        // Remove the spawn point
        plugin.getSpawnManager().removeSpawn();
        
        // Send success message
        MessageUtils.sendConfigMessage(player, "messages.spawn.spawn-removed", "Server spawn location has been removed.");
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 