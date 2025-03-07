package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PWeatherCommand extends BaseCommand {
    
    public PWeatherCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.pweather", true);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = getPlayer(sender);
        
        // Check if we have enough arguments
        if (args.length < 1) {
            MessageUtils.sendConfigMessage(player, "messages.errors.invalid-syntax", 
                    "Usage: /pweather <clear|rain|reset> [player]");
            return true;
        }
        
        // Get target player
        Player target = player;
        if (args.length > 1) {
            if (!player.hasPermission("frizzlenessentials.pweather.others")) {
                MessageUtils.sendConfigMessage(player, "messages.errors.no-permission", 
                        "You don't have permission to set weather for other players.");
                return true;
            }
            
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendConfigMessage(player, "messages.errors.player-not-found", 
                        "Player not found.");
                return true;
            }
        }
        
        // Handle reset
        if (args[0].equalsIgnoreCase("reset")) {
            target.resetPlayerWeather();
            
            if (target == player) {
                MessageUtils.sendConfigMessage(player, "messages.admin.pweather.reset", 
                        "Your weather has been reset to server weather.");
            } else {
                MessageUtils.sendConfigMessage(player, "messages.admin.pweather.reset-other", 
                        "Reset {player}'s weather to server weather.", 
                        "player", target.getName());
                
                MessageUtils.sendConfigMessage(target, "messages.admin.pweather.reset-by-other", 
                        "Your weather was reset to server weather by {player}.", 
                        "player", player.getName());
            }
            return true;
        }
        
        // Parse weather type
        WeatherType weather;
        String weatherArg = args[0].toLowerCase();
        
        switch (weatherArg) {
            case "clear":
            case "sun":
                weather = WeatherType.CLEAR;
                break;
            case "rain":
            case "storm":
                weather = WeatherType.DOWNFALL;
                break;
            default:
                MessageUtils.sendConfigMessage(player, "messages.admin.pweather.invalid-weather", 
                        "Invalid weather type. Use clear/rain/reset.");
                return true;
        }
        
        // Set the player's weather
        target.setPlayerWeather(weather);
        
        if (target == player) {
            MessageUtils.sendConfigMessage(player, "messages.admin.pweather.set", 
                    "Your weather has been set to {weather}.", 
                    "weather", weather.name().toLowerCase());
        } else {
            MessageUtils.sendConfigMessage(player, "messages.admin.pweather.set-other", 
                    "Set {player}'s weather to {weather}.", 
                    "player", target.getName(),
                    "weather", weather.name().toLowerCase());
            
            MessageUtils.sendConfigMessage(target, "messages.admin.pweather.set-by-other", 
                    "Your weather was set to {weather} by {player}.", 
                    "weather", weather.name().toLowerCase(),
                    "player", player.getName());
        }
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> weathers = List.of("clear", "rain", "reset");
            completions.addAll(weathers.stream()
                    .filter(weather -> weather.startsWith(partial))
                    .collect(Collectors.toList()));
        } else if (args.length == 2 && sender.hasPermission("frizzlenessentials.pweather.others")) {
            String partial = args[1].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList()));
        }
        
        return completions;
    }
} 