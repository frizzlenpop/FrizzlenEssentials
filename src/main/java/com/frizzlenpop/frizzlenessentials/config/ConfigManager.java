package com.frizzlenpop.frizzlenessentials.config;

import com.frizzlenpop.frizzlenessentials.FrizzlenEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {
    
    private final FrizzlenEssentials plugin;
    private FileConfiguration config;
    private Map<String, FileConfiguration> dataConfigs;
    
    // Data files
    private File homesFile;
    private File warpsFile;
    private File locationsFile;
    private File spawnFile;
    private File playersFile;
    
    // Data configs
    private FileConfiguration homesConfig;
    private FileConfiguration warpsConfig;
    private FileConfiguration locationsConfig;
    private FileConfiguration spawnConfig;
    private FileConfiguration playersConfig;
    
    public ConfigManager(FrizzlenEssentials plugin) {
        this.plugin = plugin;
        this.dataConfigs = new HashMap<>();
        
        // Load main config
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        
        // Initialize data files
        initializeDataFiles();
    }
    
    private void initializeDataFiles() {
        // Define data file paths
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        locationsFile = new File(plugin.getDataFolder(), "locations.yml");
        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        
        // Create data files if they don't exist
        createFileIfNotExists(homesFile);
        createFileIfNotExists(warpsFile);
        createFileIfNotExists(locationsFile);
        createFileIfNotExists(spawnFile);
        createFileIfNotExists(playersFile);
    }
    
    private void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                plugin.getLogger().info("Created data file: " + file.getName());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data file: " + file.getName(), e);
            }
        }
    }
    
    public void loadConfigs() {
        // Load data configurations
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        locationsConfig = YamlConfiguration.loadConfiguration(locationsFile);
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        
        // Store configs in map for easy access
        dataConfigs.put("homes", homesConfig);
        dataConfigs.put("warps", warpsConfig);
        dataConfigs.put("locations", locationsConfig);
        dataConfigs.put("spawn", spawnConfig);
        dataConfigs.put("players", playersConfig);
        
        plugin.getLogger().info("Loaded all configuration files.");
    }
    
    public void saveConfigs() {
        // Save all configuration files
        saveConfig("homes", homesFile, homesConfig);
        saveConfig("warps", warpsFile, warpsConfig);
        saveConfig("locations", locationsFile, locationsConfig);
        saveConfig("spawn", spawnFile, spawnConfig);
        saveConfig("players", playersFile, playersConfig);
        
        // Also save the main config
        plugin.saveConfig();
        
        plugin.getLogger().info("Saved all configuration files.");
    }
    
    private void saveConfig(String name, File file, FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data file: " + name, e);
        }
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        loadConfigs();
    }
    
    // Getters for data configurations
    public FileConfiguration getMainConfig() {
        return config;
    }
    
    public FileConfiguration getHomesConfig() {
        return homesConfig;
    }
    
    public FileConfiguration getWarpsConfig() {
        return warpsConfig;
    }
    
    public FileConfiguration getLocationsConfig() {
        return locationsConfig;
    }
    
    public FileConfiguration getSpawnConfig() {
        return spawnConfig;
    }
    
    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }
    
    // Helper methods to save individual configs if needed
    public void saveHomesConfig() {
        saveConfig("homes", homesFile, homesConfig);
    }
    
    public void saveWarpsConfig() {
        saveConfig("warps", warpsFile, warpsConfig);
    }
    
    public void saveLocationsConfig() {
        saveConfig("locations", locationsFile, locationsConfig);
    }
    
    public void saveSpawnConfig() {
        saveConfig("spawn", spawnFile, spawnConfig);
    }
    
    public void savePlayersConfig() {
        saveConfig("players", playersFile, playersConfig);
    }
} 