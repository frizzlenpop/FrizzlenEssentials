package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpDenyCommand extends BaseCommand {
    
    public TpDenyCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tpdeny", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Deny the teleport request
        boolean success = plugin.getTeleportManager().denyTeleportRequest(player);
        
        // Success message is handled in the TeleportManager
        if (!success) {
            // If there was an error (no pending requests, etc.), the TeleportManager will already send an error message
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