package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BackCommand extends BaseCommand {
    
    public BackCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.back", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Try to teleport the player back to their previous location
        boolean success = plugin.getTeleportManager().back(player);
        
        // Messages are handled in the TeleportManager
        if (!success) {
            // If there was an error (no previous location, etc.), the TeleportManager will already send an error message
            // So we don't need to send an additional message here
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 