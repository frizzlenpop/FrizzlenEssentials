package com.frizzlenpop.frizzlenessentials.commands;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {
    
    protected final FrizzlenEssentials plugin;
    protected final String permission;
    protected final boolean playerOnly;
    
    /**
     * Constructor for BaseCommand
     * 
     * @param plugin The plugin instance
     * @param permission The permission required to use this command
     * @param playerOnly Whether this command can only be used by players
     */
    public BaseCommand(FrizzlenEssentials plugin, String permission, boolean playerOnly) {
        this.plugin = plugin;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }
    
    /**
     * Constructor for BaseCommand with default playerOnly value of false
     * 
     * @param plugin The plugin instance
     * @param permission The permission required to use this command
     */
    public BaseCommand(FrizzlenEssentials plugin, String permission) {
        this(plugin, permission, false);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if command is player-only
        if (playerOnly && !(sender instanceof Player)) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.player-only", "This command can only be used by players.");
            return true;
        }
        
        // Check permission
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.no-permission", "You don't have permission to use this command.");
            return true;
        }
        
        // Execute the command
        return execute(sender, command, label, args);
    }
    
    /**
     * Execute the command
     * 
     * @param sender The command sender
     * @param command The command
     * @param label The command label
     * @param args The command arguments
     * @return True if the command was executed successfully, false otherwise
     */
    protected abstract boolean execute(CommandSender sender, Command command, String label, String[] args);
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Check permission
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            return new ArrayList<>();
        }
        
        return tabComplete(sender, command, alias, args);
    }
    
    /**
     * Tab complete the command
     * 
     * @param sender The command sender
     * @param command The command
     * @param alias The command alias
     * @param args The command arguments
     * @return A list of tab completions
     */
    protected List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
    
    /**
     * Get a player from the command sender
     * 
     * @param sender The command sender
     * @return The player, or null if the sender is not a player
     */
    protected Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }
    
    /**
     * Check if the command has the correct number of arguments
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @param min The minimum number of arguments
     * @param max The maximum number of arguments
     * @param usage The usage message to display if the check fails
     * @return True if the check passed, false otherwise
     */
    protected boolean checkArgs(CommandSender sender, String[] args, int min, int max, String usage) {
        if (args.length < min) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", "Invalid syntax. Usage: {usage}", "usage", usage);
            return false;
        }
        
        if (max >= 0 && args.length > max) {
            MessageUtils.sendConfigMessage(sender, "messages.errors.invalid-syntax", "Invalid syntax. Usage: {usage}", "usage", usage);
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if the command has the correct number of arguments
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @param min The minimum number of arguments
     * @param usage The usage message to display if the check fails
     * @return True if the check passed, false otherwise
     */
    protected boolean checkArgs(CommandSender sender, String[] args, int min, String usage) {
        return checkArgs(sender, args, min, -1, usage);
    }
    
    /**
     * Check if the command has the exact number of arguments
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @param count The exact number of arguments
     * @param usage The usage message to display if the check fails
     * @return True if the check passed, false otherwise
     */
    protected boolean checkArgsExact(CommandSender sender, String[] args, int count, String usage) {
        return checkArgs(sender, args, count, count, usage);
    }
} 