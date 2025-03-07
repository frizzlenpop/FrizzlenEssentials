package com.frizzlenpop.frizzlenessentials.commands.admin;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.commands.BaseCommand;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GcCommand extends BaseCommand {
    
    public GcCommand(FrizzlenEssentials plugin) {
        super(plugin, "frizzlenessentials.gc", false);
    }
    
    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        // Get initial memory usage
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Run garbage collection
        System.gc();
        
        // Get final memory usage
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Calculate freed memory
        long freedMemory = initialMemory - finalMemory;
        
        // Format memory values
        String initialMB = String.format("%.2f", initialMemory / 1024.0 / 1024.0);
        String finalMB = String.format("%.2f", finalMemory / 1024.0 / 1024.0);
        String freedMB = String.format("%.2f", freedMemory / 1024.0 / 1024.0);
        
        // Send memory usage information
        MessageUtils.sendConfigMessage(sender, "messages.admin.gc.before", 
                "Memory before GC: {memory}MB", 
                "memory", initialMB);
        
        MessageUtils.sendConfigMessage(sender, "messages.admin.gc.after", 
                "Memory after GC: {memory}MB", 
                "memory", finalMB);
        
        MessageUtils.sendConfigMessage(sender, "messages.admin.gc.freed", 
                "Memory freed: {memory}MB", 
                "memory", freedMB);
        
        return true;
    }
    
    @Override
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 