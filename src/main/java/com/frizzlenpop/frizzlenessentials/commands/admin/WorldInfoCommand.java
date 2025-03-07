package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameRule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldInfoCommand extends BaseCommand {
    
    public WorldInfoCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.worldinfo", false);
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
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }
        
        // Display world information
        sender.sendMessage("§6World Information: §f" + world.getName());
        sender.sendMessage("§7- Environment: §f" + world.getEnvironment().name());
        sender.sendMessage("§7- Difficulty: §f" + world.getDifficulty().name());
        sender.sendMessage("§7- Seed: §f" + world.getSeed());
        sender.sendMessage("§7- Time: §f" + world.getTime());
        sender.sendMessage("§7- Full Time: §f" + world.getFullTime());
        sender.sendMessage("§7- Weather Duration: §f" + world.getWeatherDuration());
        sender.sendMessage("§7- Thunder Duration: §f" + world.getThunderDuration());
        sender.sendMessage("§7- Game Rules:");
        
        // Display game rules
        for (GameRule<?> rule : GameRule.values()) {
            Object value = world.getGameRuleValue(rule);
            if (value != null) {
                sender.sendMessage("  §7- " + rule.getName() + ": §f" + value);
            }
        }
        
        // Display world border info
        sender.sendMessage("§7- World Border:");
        sender.sendMessage("  §7- Center: §f" + formatLocation(world.getWorldBorder().getCenter()));
        sender.sendMessage("  §7- Size: §f" + world.getWorldBorder().getSize());
        sender.sendMessage("  §7- Damage Buffer: §f" + world.getWorldBorder().getDamageBuffer());
        sender.sendMessage("  §7- Damage Amount: §f" + world.getWorldBorder().getDamageAmount());
        sender.sendMessage("  §7- Warning Distance: §f" + world.getWorldBorder().getWarningDistance());
        sender.sendMessage("  §7- Warning Time: §f" + world.getWorldBorder().getWarningTime());
        
        // Display entity counts
        sender.sendMessage("§7- Entities:");
        sender.sendMessage("  §7- Players: §f" + world.getPlayers().size());
        sender.sendMessage("  §7- Living Entities: §f" + world.getLivingEntities().size());
        sender.sendMessage("  §7- Loaded Chunks: §f" + world.getLoadedChunks().length);
        
        return true;
    }
    
    private String formatLocation(org.bukkit.Location location) {
        return String.format("%.2f, %.2f, %.2f", 
                location.getX(), 
                location.getY(), 
                location.getZ());
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