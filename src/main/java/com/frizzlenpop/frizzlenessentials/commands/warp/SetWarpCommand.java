package com.frizzlenpop.frizzlenessentials.commands.warp;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetWarpCommand extends BaseCommand {
    
    public SetWarpCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.setwarp", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if the command has the correct number of arguments
        if (!checkArgs(player, args, 1, "/setwarp <name>")) {
            return true;
        }
        
        String warpName = args[0];
        Location location = player.getLocation();
        
        // Try to create the warp
        boolean success = plugin.getWarpManager().createWarp(player, warpName, location);
        
        // The warp manager will send appropriate messages on success/failure
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
} 