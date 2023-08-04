package me.ilsommo.openpit.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ilsommo.openpit.ThePit;

public class KillCounter {
	
	public void calculateAndSaveKills() {
	    ThePit thePitPlugin = ThePit.getInstance();

	    // Iterate through all online players
	    for (Player player : Bukkit.getOnlinePlayers()) {
	        UUID playerUUID = player.getUniqueId();
	        double playerLevel = thePitPlugin.getPlayerLevel(playerUUID);

	        // Calculate the number of kills (4 kills per level)
	        int numberOfKills = (int) (playerLevel * 4);

	        // Create or update the killcounter.yml file
	        File killCounterFile = new File(thePitPlugin.getDataFolder(), "killcounter.yml");
	        FileConfiguration killCounterConfig = YamlConfiguration.loadConfiguration(killCounterFile);

	        // Set the number of kills for the player in the killcounter.yml file
	        killCounterConfig.set(player.getName(), numberOfKills);

	        try {
	            killCounterConfig.save(killCounterFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

}
