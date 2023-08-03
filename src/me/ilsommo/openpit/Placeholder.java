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
        if(identifier.equalsIgnoreCase("date")){
            return date;
        }
        if(identifier.equalsIgnoreCase("stat")){
            return methods.getCombat(player);
        }
        if(identifier.equalsIgnoreCase("gold")){
        	UUID playerUUID = player.getUniqueId();
            return String.valueOf(ThePit.getInstance().getGoldManager().getGold(playerUUID)); //Bypassing Methods Class
        }
        if (identifier.equalsIgnoreCase("playerlevel")) {
            if (player != null) {
                UUID playerUUID = player.getUniqueId();
                int playerLevel = ThePit.getInstance().getPlayerLevelToInt(playerUUID);
                return String.valueOf(playerLevel);
            }
        }
        return null;
    }
    
}
 