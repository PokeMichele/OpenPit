package me.ilsommo.openpit.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.enchants.enchants.PantEnchantTypes;

public class Methods {

	private ThePit main = ThePit.getPlugin(ThePit.class);
	private FileConfiguration config;
	public ArrayList<Player> combatlog = new ArrayList<Player>();
	private Messages messages;
    public ExtraConfigs data = new ExtraConfigs(main, "data.yml");

    private String b = "playerData";
    public boolean event;
    public HashMap<Player, HashMap<Integer, String>> perk1 = new HashMap<>();
    public HashMap<UUID, Integer> dataSlot = new HashMap<>();

    public Methods(ThePit main) {
        this.main = main;
        this.messages = main.getMessages();
        this.config = main.getConfig();
    }
	 /*
	 * @param  p  the player to check their combat tag 
	 */
	public String getCombat(Player p) {
		if (event == true) {
			return config.getString("Messages.EventLog");
		}
		if (combatlog.contains(p)) {
			return config.getString("Messages.Fighting");
		}
		return config.getString("Messages.Idle");		
	}
	 /**
	 * Changes the prestige of a player (usually when leveling up 
	 * @param  url  an absolute URL giving the base location of the image
	 * @param  name the location of the image, relative to the url argument
	 * @return      the image at the specified URL
	 * @see         Image
	 */
	public void changePrestige(Player p) {
		if (p.getLevel() >= 100) {
			config.set("Data." + p.getName(), config.getInt("Data." + p.getName()) + 1);
			p.sendMessage(config.getString("Messages.PrestigeReset"));
			p.setLevel(0);
			main.saveDefaultConfig();
		}
		else return;
	}
	public ItemStack createGuiItem(String name, ArrayList<String> desc, Material material) {
		ItemStack i = new ItemStack(material, 1);
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);	
		return i;
	}
	public ItemStack createGuiItem(String name, ArrayList<String> s, ItemStack itemStack) {
		ItemStack i = itemStack;
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(s);
		i.setItemMeta(iMeta);	
		return i;		
	}
	public String place(String string, Player p) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
			return PlaceholderAPI.setPlaceholders(p, string);
		}
	    Date now = new Date();
	    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	    String date = format.format(now);
		return ChatColor.translateAlternateColorCodes('&', string
				.replace("%thepit_name%", p.getName())
				.replace("%thepit_stat%", getCombat(p))
				.replace("%thepit_date%", date)
				.replace("%thepit_gold%", String.valueOf(getGold(p))));
	}

	public Collection<? extends String> place(List<String> list, Player p) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
			return PlaceholderAPI.setPlaceholders(p, list);
		}
		List<String> strings = new ArrayList<>();
	    Date now = new Date();
	    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	    String date = format.format(now);
	    for (String string : list) {
		strings.add(placeholderWithout(string, p));
	    }
	    return strings;
	}
	private String placeholderWithout(String string, Player p) {
	    Date now = new Date();
	    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	    String date = format.format(now);
		return ChatColor.translateAlternateColorCodes('&', string
				.replace("%thepit_name%", p.getName())
				.replace("%thepit_stat%", getCombat(p))
				.replace("%thepit_date%", date)
				.replace("%thepit_gold%", String.valueOf(getGold(p))));
				//%thepit_playerlevel% non necessario per questo script
		}
	private Location originalHologramLocation;
	public void createHolo(Player p) {
		File killCounterFile = new File(ThePit.getInstance().getDataFolder(), "killcounter.yml");
        if (!killCounterFile.exists()) {
            ThePit.getInstance().getLogger().warning("Kill counter file (killcounter.yml) not found.");
            return;
        }
        FileConfiguration killCounterConfig = YamlConfiguration.loadConfiguration(killCounterFile);
        Map<String, Integer> playerKills = new HashMap<>();

        for (String playerName : killCounterConfig.getKeys(false)) {
            int kills = killCounterConfig.getInt(playerName);
            playerKills.put(playerName, kills);
        }

        List<Map.Entry<String, Integer>> sortedKills = new ArrayList<>(playerKills.entrySet());
        sortedKills.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        List<String> hologramLines = new ArrayList<>();
        int maxLines = 10;

        hologramLines.add(ChatColor.AQUA + "TOP");
        for (int i = 0; i < Math.min(maxLines, sortedKills.size()); i++) {
            Map.Entry<String, Integer> entry = sortedKills.get(i);
            String playerName = entry.getKey();
            int kills = entry.getValue();
            hologramLines.add(playerName + ": " + kills);
        }
        Hologram hologram = DHAPI.createHologram("top_kills", p.getLocation(), true, hologramLines);
        if (originalHologramLocation == null) {
            originalHologramLocation = DHAPI.getHologram("top_kills").getLocation();
        }
		}
	
	public void updateHologram() {
	    ThePit thePitPlugin = ThePit.getInstance();
	    File killCounterFile = new File(thePitPlugin.getDataFolder(), "killcounter.yml");
	    if (!killCounterFile.exists()) {
	        thePitPlugin.getLogger().warning("Kill counter file (killcounter.yml) not found.");
	        return;
	    }

	    FileConfiguration killCounterConfig = YamlConfiguration.loadConfiguration(killCounterFile);
	    Map<String, Integer> playerKills = new HashMap<>();

	    // Iterate through the keys (player names) in the killcounter.yml file and update the playerKills map
	    for (String playerName : killCounterConfig.getKeys(false)) {
	        int kills = killCounterConfig.getInt(playerName);
	        playerKills.put(playerName, kills);
	    }

	    // Re-sort the playerKills map based on the updated statistics
	    List<Map.Entry<String, Integer>> sortedKills = new ArrayList<>(playerKills.entrySet());
	    sortedKills.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

	    List<String> hologramLines = new ArrayList<>();
	    int maxLines = 10;

	    hologramLines.add(ChatColor.AQUA + "TOP");
	    for (int i = 0; i < Math.min(maxLines, sortedKills.size()); i++) {
	        Map.Entry<String, Integer> entry = sortedKills.get(i);
	        String playerName = entry.getKey();
	        int kills = entry.getValue();
	        hologramLines.add(playerName + ": " + kills);
	    }
	    
	    // Create or update the hologram with the new data
	    if (!DHAPI.getHologram("top_kills").isEnabled()) {
	    	DHAPI.createHologram("top_kills", originalHologramLocation, true, hologramLines);
	    }
	    else {
	    	DHAPI.removeHologram("top_kills");
	    	DHAPI.createHologram("top_kills", originalHologramLocation, true, hologramLines);
	    }
	}

		public ItemStack setItemName(ItemStack i, List<String> list, PantEnchantTypes en, Player p, boolean t) {
			//XTag will NEVER be null, custom items always have this data. Null check removed.
	       			
			switch(String.valueOf(XTags.getItemTag(i, "Tier"))) {
	        case "Tier I":
		        i = XTags.setItemTag(i, "Tier II", "Tier");
		        i = XTags.setItemTag(i, 8, "Lives");
		        i = XTags.setItemTag(i, 8, "MaxLives");
		        i = XTags.setItemTag(i, 4000, "Cost"); 

		        break;
	        case "Tier II":
		        i = XTags.setItemTag(i, "Tier III", "Tier");
		        i = XTags.setItemTag(i, 10, "Lives");
		        i = XTags.setItemTag(i, 10, "MaxLives");
		        i = XTags.setItemTag(i, 8000, "Cost"); 
		        break;
	        case "Tier III":
		        i = XTags.setItemTag(i, "MAXED OUT", "Tier");
		        i = XTags.setItemTag(i, 15, "MaxLives");
		        i = XTags.setItemTag(i, "false", "Enchantable"); 
		        i = XTags.setItemTag(i, 15, "Lives"); 
		        break;
		    default:
		    	p.sendMessage(ChatColor.RED + "Maximum enchanting limit reached on this item.");
		    	return i;
	        }
			ItemMeta itemMeta = i.getItemMeta();
			
			list.set(0, ChatColor.GREEN + String.valueOf(XTags.getItemTag(i, "Lives")) + ChatColor.GRAY + "/" + String.valueOf(XTags.getItemTag(i, "MaxLives")));
	        for (String s : main.getConfig().getStringList("EnchantmentTypes." + String.valueOf(en) + ".Lore")) {
	        	if (s == null) {
	        		break;
	        	}
	        	list.add(ChatColor.translateAlternateColorCodes('&', s));
	        }
	        itemMeta.setLore(list);
	        i.setItemMeta(itemMeta);
	        i = XTags.setItemTag(i, "true", String.valueOf(en));
	        return i;
	        
		}

		public void setGold(Player p, int amount) {
	        if (amount > 999999999) {
	            p.sendMessage(ChatColor.RED + "MAXIMUM AMOUNT IS 999,999,999");
	            return;
	        }
	        UUID playerUUID = p.getUniqueId();
	        ThePit.getInstance().getGoldManager().addGold(playerUUID, amount);
	    } 

	    public boolean removeGold(Player p, int amount) {
	        UUID playerUUID = p.getUniqueId();
	        int currentGold = ThePit.getInstance().getGoldManager().getGold(playerUUID);
	        
	        if (currentGold - amount < 0) {
	            p.sendMessage(ChatColor.RED + "Not enough gold.");
	            return false;
	        }
	        
	        ThePit.getInstance().getGoldManager().addGold(playerUUID, -amount);
	        return true;
	    }

	    public int getGold(Player p) {
	        UUID playerUUID = p.getUniqueId();
	        return ThePit.getInstance().getGoldManager().getGold(playerUUID);
	    }
}