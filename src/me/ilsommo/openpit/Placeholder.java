package me.ilsommo.openpit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ilsommo.openpit.utils.Methods;

public class Placeholder extends PlaceholderExpansion {

	private Methods methods;
	
	private FileConfiguration config;

    public Placeholder(ThePit main) {
        this.methods = main.getMethods(); // Get the Methods object from the main instance
    }

    public String getIdentifier() {
        return "thepit";
    }
    public String getPlugin() {
        return null;
    }
    public String getAuthor() {
        return "Il Sommo";
    }
    public String getVersion() {
        return "version1";
    }
    public int getPrestige(Player p) {
	    int prestige = 0;
	    if (config.contains("Data." + p.getName())) {
	        prestige = config.getInt("Data." + p.getName());
	    }

	    // Logging per verificare quale valore di prestigio viene restituito
	    Bukkit.getLogger().info("Player " + p.getName() + " has prestige level: " + prestige);

	    return prestige;
	}
    public String getChatLevel(Player p) {
		if (getPrestige(p) >= 4) {
	        return (ChatColor.AQUA + "[" + ChatColor.GRAY + levelColor(p) + ChatColor.AQUA + "]");
	    }
		switch(getPrestige(p)) {
		case 1:
			return (ChatColor.BLUE + "[" + ChatColor.GRAY + levelColor(p) + ChatColor.BLUE + "]");
		case 2:
			return (ChatColor.WHITE + "[" + ChatColor.GRAY + levelColor(p) + ChatColor.WHITE + "]");
		case 3:
			return (ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + levelColor(p) + ChatColor.DARK_GRAY + "]");
		default:
			return (ChatColor.GRAY + "[" + ChatColor.GRAY + levelColor(p) + ChatColor.GRAY + "]");
		}
	}
    public String levelColor(Player p) { //TODO: EDIT ALL THE IF STATEMENTS - LOOKS CLUTTERED
		String str = null;
		String level = String.valueOf(p.getLevel());
		int levels = p.getLevel();
		if (levels >= 0 && levels < 10) {
			return str = (ChatColor.GRAY + level);
		}
		if (levels >= 10 && levels < 20) {
			return str = (ChatColor.RED + level);
		}
		if (levels >= 20 && levels < 30) {
			return str = (ChatColor.AQUA + level);
		}
		if (levels >= 30 && levels < 40) {
			return str = (ChatColor.DARK_AQUA + level);
		}
		if (levels >= 40 && levels < 50) {
			return str = (ChatColor.LIGHT_PURPLE + level);
		}
		if (levels >= 50 && levels < 60) {
			return str = (ChatColor.GREEN + level);
		}
		if (levels >= 60) {
			return str = (ChatColor.WHITE + level); 
		}
		return str;
	}
    public String onPlaceholderRequest(Player player, String identifier) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String date = format.format(now);
        if(identifier.equalsIgnoreCase("onlines")){
            return String.valueOf(Bukkit.getOnlinePlayers().size());
        }
        if(player == null){
            return "";
        }
        if(identifier.equalsIgnoreCase("name")){
            return player.getName();
        }
        if(identifier.equalsIgnoreCase("level")){
            return getChatLevel(player);  //Bypassing Methods Class
        }
        if(identifier.equalsIgnoreCase("date")){
            return date;
        }
        if(identifier.equalsIgnoreCase("xp")){
            return String.valueOf(player.getExpToLevel());
        }
        if(identifier.equalsIgnoreCase("stat")){
            return methods.getCombat(player);
        }
        if(identifier.equalsIgnoreCase("gold")){
        	UUID playerUUID = player.getUniqueId();
            return String.valueOf(ThePit.getInstance().getGoldManager().getGold(playerUUID)); //Bypassing Methods Class
        }
 
        return null;
    }
    
}
 