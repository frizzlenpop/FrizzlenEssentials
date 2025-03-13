package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UptimeCommand extends BaseCommand {
    
    public UptimeCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.uptime", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Get uptime in milliseconds
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        
        // Convert to days, hours, minutes, seconds
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        
        // Build the uptime message
        StringBuilder message = new StringBuilder();
        
        if (days > 0) {
            message.append(days).append(" day").append(days != 1 ? "s" : "").append(" ");
        }
        if (hours > 0) {
            message.append(hours).append(" hour").append(hours != 1 ? "s" : "").append(" ");
        }
        if (minutes > 0) {
            message.append(minutes).append(" minute").append(minutes != 1 ? "s" : "").append(" ");
        }
        if (seconds > 0 || message.length() == 0) {
            message.append(seconds).append(" second").append(seconds != 1 ? "s" : "");
        }
        
        // Send the uptime message
        MessageUtils.sendConfigMessage(sender, "messages.admin.uptime", 
                "Server has been running for {time}.", 
                "uptime", message.toString().trim());
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
} 