package me.ilsommo.openpit;

import me.ilsommo.deaths.InstantDeath;
import me.ilsommo.extraevents.CombatLog;
import me.ilsommo.extraevents.GoldenHeads;
import me.ilsommo.extraevents.HealthBar;
import me.ilsommo.extraevents.PlaceEvents;
import me.ilsommo.extraevents.PlayerDamage;
import me.ilsommo.extraevents.Streaks;
import me.ilsommo.openpit.commands.CommandModule;
import me.ilsommo.openpit.commands.Commands;
import me.ilsommo.openpit.commands.TabComplete;
import me.ilsommo.openpit.commands.subcommands.Gold;
import me.ilsommo.openpit.enchants.pants.PantsEnchants;
import me.ilsommo.openpit.gui.mysticwell.MysticWell;
import me.ilsommo.openpit.gui.perks.Perks;
import me.ilsommo.openpit.gui.perks.PerksMainMenu;
import me.ilsommo.openpit.gui.shop.Shop;
import me.ilsommo.openpit.packets.PacketUtil;
import me.ilsommo.openpit.serverevents.ServerEventsRunnable;
import me.ilsommo.openpit.utils.ArmorListener;
import me.ilsommo.openpit.utils.ExtraConfigs;
import me.ilsommo.openpit.utils.GoldManager;
import me.ilsommo.openpit.utils.GoldPickupListener;
import me.ilsommo.openpit.utils.KillCounter;
import me.ilsommo.openpit.utils.Messages;
import me.ilsommo.openpit.utils.Methods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ThePit extends JavaPlugin {

    public static String v = null;
    private Methods methods;
    private Messages messages;
    private PacketUtil packets;
    private Streaks streaks;
    private PerksMainMenu perks;
    private Perks perks2;
    private Shop shop;
    public ExtraConfigs guis;
    public ExtraConfigs vaults;
    public static HashMap<String, CommandModule> commands;
    private static ThePit instance;
    private GoldManager goldManager;
    private FileConfiguration levelsConfig;
    private File levelsFile;

    private PluginManager pm;
    private ConsoleCommandSender sender;

    @Override
    public void onEnable() {
        v = Bukkit.getServer().getClass().getPackage().getName();
        v = v.substring(v.lastIndexOf(".") + 1);

        pm = Bukkit.getPluginManager();
        sender = Bukkit.getConsoleSender();

        commands = new HashMap<>();
        guis = new ExtraConfigs(this, "guis.yml", "guis.yml");
        vaults = new ExtraConfigs(this, "vaults.yml", "vaults.yml");

        if (pm.isPluginEnabled("PlaceholderAPI")) {
            new Placeholder(this).register(); // Pass 'this' as an argument to the constructor
            sender.sendMessage(ChatColor.GREEN + "PlaceholderAPI found, working with it.");
        } else {
            sender.sendMessage(ChatColor.RED + "PlaceholderAPI not found, working without it.");
        }
        levelsFile = new File(getDataFolder(), "levels.yml");
        if (!levelsFile.exists()) {
            saveResource("levels.yml", false);
        }
        levelsConfig = YamlConfiguration.loadConfiguration(levelsFile);
        
        instance = this;
        goldManager = new GoldManager();
        methods = new Methods(this);
        
        createOrUpdateKillCounterFile();
        calculateAndSaveKills();
        int updateIntervalInMinutes = 5;
        int updateIntervalInTicks = updateIntervalInMinutes * 60 * 20;
        Bukkit.getScheduler().runTaskTimer(this, this::calculateAndSaveKills, updateIntervalInTicks, updateIntervalInTicks);
        BukkitRunnable hologramUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateHologram();
                System.out.println("DEBUG: Hologram updated successfully");
            }
        };
        hologramUpdater.runTaskTimer(this, updateIntervalInTicks, updateIntervalInTicks);
        
        registerEvents(); // Aggiungi questa linea per registrare gli eventi
        disableEntities();

        // Registra il tuo executor di comando per il comando /thepit
        getCommand("openpit").setExecutor(new Commands(this));
        getCommand("openpit").setTabCompleter(new TabComplete());
        getCommand("gold").setExecutor(new Gold(this));
        getCommand("perks").setExecutor(new MysticWell(this));

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        startEvents();
    }

    @Override
    public void onDisable() {
    	// Salva i dati dell'oro nel file "gold.yml" quando il plugin viene disabilitato
        goldManager.saveData();
    }
    
    public static ThePit getInstance() {
        return instance;
    }
    
    public GoldManager getGoldManager() {
        return goldManager;
    }

    // INSTANCES ---
    public Methods getMethods() {
        return this.methods;
    }

    public Messages getMessages() {
        return this.messages;
    }

    public PacketUtil getPackets() {
        return this.packets;
    }

    public Streaks getStreaks() {
        return this.streaks;
    }

    public PerksMainMenu getPerks() {
        return this.perks;
    }
    
    public Perks getPerks2() {
        return this.perks2;
    }

    public Shop getShop() {
        return this.shop;
    }

    // MAIN METHODS
    private void disableEntities() {
        // Disabling villager movement
        for (World world : Bukkit.getWorlds()) {
            for (Entity en : world.getEntities()) {
                if (en instanceof Villager) {
                    try {
                        packets.noAI(en, en.getLocation());
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                            | SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void registerEvents() {
        pm.registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
        pm.registerEvents(new Perks(this), this);
        pm.registerEvents(new GoldPickupListener(), this);
        pm.registerEvents(new Shop(this), this);
        this.methods = new Methods(this);
        this.messages = new Messages(this);
        this.packets = new PacketUtil(this);
        this.streaks = new Streaks(this);
        this.perks = new PerksMainMenu(this);

        new CombatLog(this);
        new GoldenHeads(this);
        new PlaceEvents(this);
        new InstantDeath(this);
        new HealthBar(this);
        new PantsEnchants(this);
        new PlayerDamage(this);

        sender.sendMessage(ChatColor.GREEN + "Events Registered");
    }

    private void startEvents() {
        ServerEventsRunnable r = new ServerEventsRunnable(this);
        r.runTaskTimer(this, 100L, 100L);
    }
    private void createOrUpdateKillCounterFile() {
        File killCounterFile = new File(getDataFolder(), "killcounter.yml");
        if (!killCounterFile.exists()) {
            // If the file doesn't exist, create a new empty configuration
            FileConfiguration killCounterConfig = new YamlConfiguration();
            try {
                killCounterConfig.save(killCounterFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Update Hologram Method
    private void updateHologram() {
       this.getMethods().updateHologram();
    }
    //Method to calculate and save the kills for all players
    private void calculateAndSaveKills() {
        KillCounter killCounter = new KillCounter();
        killCounter.calculateAndSaveKills();
    }
    // Metodo per salvare i livelli su levels.yml
    public void savePlayerLevel(UUID playerUUID, double newLevel) {
        levelsConfig.set(playerUUID.toString(), newLevel);

        try {
            levelsConfig.save(levelsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 // Metodo per recuperare il livello di un giocatore dal file levels.yml
    public double getPlayerLevel(UUID playerUUID) {
        if (levelsConfig.contains(playerUUID.toString())) {
            return levelsConfig.getDouble(playerUUID.toString());
        }
        return 0; // Restituisce il livello 0 se il giocatore non ha un livello salvato
    }
    public int getPlayerLevelToInt(UUID playerUUID) {
        if (levelsConfig.contains(playerUUID.toString())) {
            return (int) levelsConfig.getDouble(playerUUID.toString());
        }
        return 0; // Restituisce il livello 0 se il giocatore non ha un livello salvato
    }
}