package com.frizzlenpop.frizzlenessentials.utils;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private static final Pattern COLOR_PATTERN = Pattern.compile("\\{([A-Z_]+)\\}");
    private static FrizzlenEssentials plugin;
    private static Map<String, String> colorMap;
    
    public static void initialize(FrizzlenEssentials instance) {
        plugin = instance;
        colorMap = new HashMap<>();
        loadColors();
    }
    
    private static void loadColors() {
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        String prefix = config.getString("messages.prefix", "&7[&aFrizzlenEssentials&7] &r");
        
        colorMap.put("PREFIX", ChatColor.translateAlternateColorCodes('&', prefix));
        colorMap.put("PRIMARY", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.primary", "&a")));
        colorMap.put("SECONDARY", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.secondary", "&7")));
        colorMap.put("HIGHLIGHT", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.highlight", "&e")));
        colorMap.put("ERROR", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.error", "&c")));
        colorMap.put("SUCCESS", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.success", "&a")));
        colorMap.put("WARNING", ChatColor.translateAlternateColorCodes('&', config.getString("messages.colors.warning", "&6")));
    }
    
    /**
     * Format a message by replacing color placeholders and applying color codes
     * 
     * @param message The message to format
     * @return The formatted message
     */
    public static String formatMessage(String message) {
        if (message == null) return "";
        
        // Replace color placeholders
        Matcher matcher = COLOR_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String colorKey = matcher.group(1);
            String replacement = colorMap.getOrDefault(colorKey, "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        
        // Translate color codes
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    
    /**
     * Format a message with replacements
     * 
     * @param message The message to format
     * @param replacements Key-value pairs for replacements (e.g., "player", "Steve")
     * @return The formatted message with replacements
     */
    public static String formatMessage(String message, Object... replacements) {
        if (message == null) return "";
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be key-value pairs");
        }
        
        String formattedMessage = message;
        
        // Apply replacements
        for (int i = 0; i < replacements.length; i += 2) {
            String key = String.valueOf(replacements[i]);
            String value = String.valueOf(replacements[i + 1]);
            formattedMessage = formattedMessage.replace("{" + key + "}", value);
        }
        
        // Format colors
        return formatMessage(formattedMessage);
    }
    
    /**
     * Get a message from the config
     * 
     * @param path The path to the message in the config
     * @param defaultMessage The default message if the path doesn't exist
     * @return The formatted message
     */
    public static String getMessage(String path, String defaultMessage) {
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        String message = config.getString(path, defaultMessage);
        return formatMessage(message);
    }
    
    /**
     * Get a message from the config with replacements
     * 
     * @param path The path to the message in the config
     * @param defaultMessage The default message if the path doesn't exist
     * @param replacements Key-value pairs for replacements
     * @return The formatted message with replacements
     */
    public static String getMessage(String path, String defaultMessage, Object... replacements) {
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        String message = config.getString(path, defaultMessage);
        return formatMessage(message, replacements);
    }
    
    /**
     * Send a message to a command sender
     * 
     * @param sender The command sender
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender != null && message != null && !message.isEmpty()) {
            sender.sendMessage(colorMap.get("PREFIX") + formatMessage(message));
        }
    }
    
    /**
     * Send a message from the config to a command sender
     * 
     * @param sender The command sender
     * @param path The path to the message in the config
     * @param defaultMessage The default message if the path doesn't exist
     */
    public static void sendConfigMessage(CommandSender sender, String path, String defaultMessage) {
        sendMessage(sender, getMessage(path, defaultMessage));
    }
    
    /**
     * Send a message from the config to a command sender with replacements
     * 
     * @param sender The command sender
     * @param path The path to the message in the config
     * @param defaultMessage The default message if the path doesn't exist
     * @param replacements Key-value pairs for replacements
     */
    public static void sendConfigMessage(CommandSender sender, String path, String defaultMessage, Object... replacements) {
        sendMessage(sender, getMessage(path, defaultMessage, replacements));
    }
    
    /**
     * Send an error message to a command sender
     * 
     * @param sender The command sender
     * @param message The error message
     */
    public static void sendErrorMessage(CommandSender sender, String message) {
        if (sender != null && message != null && !message.isEmpty()) {
            sender.sendMessage(colorMap.get("PREFIX") + colorMap.get("ERROR") + formatMessage(message));
        }
    }
    
    /**
     * Send a success message to a command sender
     * 
     * @param sender The command sender
     * @param message The success message
     */
    public static void sendSuccessMessage(CommandSender sender, String message) {
        if (sender != null && message != null && !message.isEmpty()) {
            sender.sendMessage(colorMap.get("PREFIX") + colorMap.get("SUCCESS") + formatMessage(message));
        }
    }
} 