package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpAllCommand extends BaseCommand {
    
    public TpAllCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tpall", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Teleport all players to the sender
        int count = plugin.getTeleportManager().teleportAll(player.getLocation(), player);
        
        if (count > 0) {
            MessageUtils.sendConfigMessage(player, "messages.teleport.all-players-teleported", 
                    "Teleported {count} players to your location.", 
                    "count", String.valueOf(count));
        } else {
            MessageUtils.sendConfigMessage(player, "messages.teleport.no-players-to-teleport", 
                    "No other players to teleport.");
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 