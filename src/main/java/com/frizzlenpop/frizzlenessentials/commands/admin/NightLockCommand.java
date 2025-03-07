package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NightLockCommand extends BaseCommand {
    
    public NightLockCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.nightlock", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Get target world
        World world;
        if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
            if (world == null) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.world-not-found", 
                        "World not found.");
                return true;
            }
        } else {
            // Use the first world if no world is specified
            world = Bukkit.getWorlds().get(0);
        }
        
        // Toggle night lock
        boolean locked = !plugin.getConfigManager().getMainConfig().getBoolean("time-lock." + world.getName() + ".night", false);
        
        // Update config
        plugin.getConfigManager().getMainConfig().set("time-lock." + world.getName() + ".night", locked);
        plugin.getConfigManager().getMainConfig().set("time-lock." + world.getName() + ".day", false);
        plugin.saveConfig();
        
        if (locked) {
            // Set time to night
            world.setTime(18000);
            world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.nightlock.enabled", 
                    "Night lock enabled for world {world}.", 
                    "world", world.getName());
        } else {
            // Re-enable daylight cycle
            world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, true);
            
            MessageUtils.sendConfigMessage(sender, "messages.admin.nightlock.disabled", 
                    "Night lock disabled for world {world}.", 
                    "world", world.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            completions.addAll(Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 