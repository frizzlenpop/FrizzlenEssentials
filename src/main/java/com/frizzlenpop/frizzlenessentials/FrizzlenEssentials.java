package com.frizzlenpop.frizzlenessentials;

import com.frizzlenpop.frizzlenessentials.commands.teleport.*;
import com.frizzlenpop.frizzlenessentials.commands.home.*;
import com.frizzlenpop.frizzlenessentials.commands.spawn.*;
import com.frizzlenpop.frizzlenessentials.commands.utility.*;
import com.frizzlenpop.frizzlenessentials.commands.fun.*;
import com.frizzlenpop.frizzlenessentials.commands.admin.*;
import com.frizzlenpop.frizzlenessentials.commands.warp.*;
import com.frizzlenpop.frizzlenessentials.managers.*;
import com.frizzlenpop.frizzlenessentials.listeners.*;
import com.frizzlenpop.frizzlenessentials.config.ConfigManager;
import com.frizzlenpop.frizzlenessentials.utils.MessageUtils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class FrizzlenEssentials extends JavaPlugin {
    
    private static FrizzlenEssentials instance;
    private ConfigManager configManager;
    private TeleportManager teleportManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private LocationManager locationManager;
    private SpawnManager spawnManager;
    
    @Override
    public void onEnable() {
        // Set instance for static access
        instance = this;
        
        // Initialize logger
        Logger logger = getLogger();
        
        // Display startup message
        logger.info(ChatColor.GREEN + "FrizzlenEssentials v" + getDescription().getVersion() + " is starting up!");
        
        // Load configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        
        // Initialize MessageUtils
        MessageUtils.initialize(this);
        
        // Initialize managers
        initializeManagers();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        logger.info(ChatColor.GREEN + "FrizzlenEssentials v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data before shutdown
        if (homeManager != null) {
            homeManager.saveAllHomes();
        }
        
        if (warpManager != null) {
            warpManager.saveAllWarps();
        }
        
        if (locationManager != null) {
            locationManager.saveAllLocations();
        }
        
        if (spawnManager != null) {
            // Explicitly save spawn data
            spawnManager.saveSpawn();
        }
        
        if (configManager != null) {
            configManager.saveConfigs();
        }
        
        getLogger().info(ChatColor.RED + "FrizzlenEssentials v" + getDescription().getVersion() + " has been disabled!");
        
        // Clear instance
        instance = null;
    }
    
    private void initializeManagers() {
        // Initialize all managers
        teleportManager = new TeleportManager(this);
        homeManager = new HomeManager(this);
        warpManager = new WarpManager(this);
        locationManager = new LocationManager(this);
        spawnManager = new SpawnManager(this);
    }
    
    private void registerListeners() {
        // Register all event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
    }
    
    private void registerCommands() {
        // Register teleportation commands
        registerTeleportCommands();
        
        // Register home and warp commands
        registerHomeWarpCommands();
        
        // Register spawn commands
        registerSpawnCommands();
        
        // Register player utility commands
        registerUtilityCommands();
        
        // Register fun commands
        registerFunCommands();
        
        // Register admin commands
        registerAdminCommands();
    }
    
    private void registerTeleportCommands() {
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpahere").setExecutor(new TpaHereCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        getCommand("tphere").setExecutor(new TpHereCommand(this));
        getCommand("tpall").setExecutor(new TpAllCommand(this));
        getCommand("lastdeath").setExecutor(new LastDeathCommand(this));
        getCommand("tpworld").setExecutor(new TpWorldCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("top").setExecutor(new TopCommand(this));
        getCommand("up").setExecutor(new UpCommand(this));
    }
    
    private void registerHomeWarpCommands() {
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("listhomes").setExecutor(new ListHomesCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("warpinfo").setExecutor(new WarpInfoCommand(this));
        getCommand("warpaccess").setExecutor(new WarpAccessCommand(this));
    }
    
    private void registerSpawnCommands() {
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
    }
    
    private void registerUtilityCommands() {
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("speed").setExecutor(new SpeedCommand(this));
        getCommand("walkspeed").setExecutor(new WalkSpeedCommand(this));
        getCommand("jumpboost").setExecutor(new JumpBoostCommand(this));
        getCommand("hat").setExecutor(new HatCommand(this));
        getCommand("itemname").setExecutor(new ItemNameCommand(this));
        getCommand("itemlore").setExecutor(new ItemLoreCommand(this));
        getCommand("repair").setExecutor(new RepairCommand(this));
        getCommand("stack").setExecutor(new StackCommand(this));
        getCommand("more").setExecutor(new MoreCommand(this));
    }
    
    private void registerFunCommands() {
        getCommand("sit").setExecutor(new SitCommand(this));
        getCommand("lay").setExecutor(new LayCommand(this));
        getCommand("crawl").setExecutor(new CrawlCommand(this));
        getCommand("spin").setExecutor(new SpinCommand(this));
    }
    
    private void registerAdminCommands() {
        getCommand("uptime").setExecutor(new UptimeCommand(this));
        getCommand("gc").setExecutor(new GcCommand(this));
        getCommand("debug").setExecutor(new DebugCommand(this));
        getCommand("daylock").setExecutor(new DayLockCommand(this));
        getCommand("nightlock").setExecutor(new NightLockCommand(this));
        getCommand("ptime").setExecutor(new PTimeCommand(this));
        getCommand("pweather").setExecutor(new PWeatherCommand(this));
        getCommand("worldinfo").setExecutor(new WorldInfoCommand(this));
        getCommand("sudo").setExecutor(new SudoCommand(this));
        getCommand("invsee").setExecutor(new InvSeeCommand(this));
        getCommand("endersee").setExecutor(new EnderSeeCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("glow").setExecutor(new GlowCommand(this));
    }
    
    // Getter methods for managers
    public static FrizzlenEssentials getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public WarpManager getWarpManager() {
        return warpManager;
    }
    
    public LocationManager getLocationManager() {
        return locationManager;
    }
    
    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
} 