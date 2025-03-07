package com.frizzlenpop.frizzlenessentials.commands.teleport;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TpCommand extends BaseCommand {
    
    public TpCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.tp", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", 
                    "Usage: /tp <player> [target] or /tp <x> <y> <z> [world]");
            return true;
        }
        
        // Handle teleporting to coordinates
        if (args.length >= 3 && isCoordinate(args[0])) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-only", 
                        "Only players can teleport to coordinates.");
                return true;
            }
            
            Player player = (Player) sender;
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                
                Location location = new Location(
                    args.length >= 4 ? Bukkit.getWorld(args[3]) : player.getWorld(),
                    x, y, z,
                    player.getLocation().getYaw(),
                    player.getLocation().getPitch()
                );
                
                if (location.getWorld() == null) {
                    MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-world", 
                            "Invalid world specified.");
                    return true;
                }
                
                boolean success = plugin.getTeleportManager().teleport(player, location);
                if (success) {
                    MessageUtils.sendConfigMessage(player, "messages.teleport.teleported-to-location", 
                            "Teleported to {x}, {y}, {z} in {world}.", 
                            "x", String.format("%.2f", x),
                            "y", String.format("%.2f", y),
                            "z", String.format("%.2f", z),
                            "world", location.getWorld().getName());
                }
                return true;
            } catch (NumberFormatException e) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-coordinates", 
                        "Invalid coordinates specified.");
                return true;
            }
        }
        
        // Handle teleporting between players
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        // If only one player is specified, teleport the sender to the target
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.player-only", 
                        "Console cannot teleport to players.");
                return true;
            }
            
            Player player = (Player) sender;
            if (player.equals(target)) {
                MessageUtils.sendConfigMessage(sender, "messages.errors.cannot-teleport-to-self", 
                        "You cannot teleport to yourself.");
                return true;
            }
            
            boolean success = plugin.getTeleportManager().teleport(player, target.getLocation());
            if (success) {
                MessageUtils.sendConfigMessage(player, "messages.teleport.teleported-to-player", 
                        "Teleported to {player}.", "player", target.getName());
            }
            return true;
        }
        
        // If two players are specified, teleport the first player to the second player
        Player destination = Bukkit.getPlayer(args[1]);
        if (destination == null) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.player-not-found", 
                    "Player not found.");
            return true;
        }
        
        if (target.equals(destination)) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.cannot-teleport-to-self", 
                    "Cannot teleport a player to themselves.");
            return true;
        }
        
        boolean success = plugin.getTeleportManager().teleport(target, destination.getLocation());
        if (success) {
            MessageUtils.sendConfigMessage(sender, "messages.teleport.teleported-player-to-player", 
                    "Teleported {player} to {target}.", 
                    "player", target.getName(),
                    "target", destination.getName());
            
            MessageUtils.sendConfigMessage(target, "messages.teleport.teleported-by-admin", 
                    "You were teleported to {player} by {admin}.", 
                    "player", destination.getName(),
                    "admin", sender.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument can be either a player name or coordinate
            String partialName = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
            
            // Add coordinate suggestion if it looks like they're typing a number
            if (partialName.matches("-?\\d*\\.?\\d*")) {
                completions.add("~");
            }
        } else if (args.length == 2) {
            // If first argument was a player, suggest other players
            // If first argument was a coordinate, suggest another coordinate
            if (isCoordinate(args[0])) {
                if (args[1].matches("-?\\d*\\.?\\d*")) {
                    completions.add("~");
                }
            } else {
                String partialName = args[1].toLowerCase();
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partialName))
                        .collect(Collectors.toList()));
            }
        } else if (args.length == 3 && isCoordinate(args[0]) && isCoordinate(args[1])) {
            // Suggest z coordinate
            if (args[2].matches("-?\\d*\\.?\\d*")) {
                completions.add("~");
            }
        } else if (args.length == 4 && isCoordinate(args[0]) && isCoordinate(args[1]) && isCoordinate(args[2])) {
            // Suggest worlds
            String partialWorld = args[3].toLowerCase();
            completions.addAll(Bukkit.getWorlds().stream()
                    .map(world -> world.getName())
                    .filter(name -> name.toLowerCase().startsWith(partialWorld))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
    
    private boolean isCoordinate(String arg) {
        return arg.matches("-?\\d*\\.?\\d*") || arg.equals("~");
    }
} 