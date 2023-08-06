package me.ilsommo.deaths;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.gui.perks.Perks;
import me.ilsommo.openpit.packets.PacketUtil;
import me.ilsommo.openpit.utils.Messages;

public class InstantDeath implements Listener {
  
	private ThePit main;
	private PacketUtil packets;
	private FileConfiguration config;
	private Messages messages;
	private Perks perks2;
	
	public InstantDeath(ThePit main) {
		this.main = main;
		this.messages = main.getMessages();
		this.packets = main.getPackets();
		this.perks2 = main.getPerks2();
		this.config = main.getConfig();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player p = e.getEntity();
		e.getDrops().clear();
		if (p.getKiller() != null) {
            Player killer = p.getKiller();
            double newLevel = main.getPlayerLevel(killer.getUniqueId()) + 0.25;
            main.savePlayerLevel(killer.getUniqueId(), newLevel);
        }
	}
	
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		Location spawnPoint = p.getWorld().getSpawnLocation();
		p.teleport(spawnPoint);
	}

	@EventHandler
	public void onPlayerPreAutoRespawn(PlayerPreAutoRespawnEvent e) {
	    if (!config.getBoolean("enabled")) return;
	    
	    Player player = e.getPlayer();
	    Location deathLoc = player.getLocation();
	    List<String> worlds = config.getStringList("blocked-worlds");
	    
	    if (worlds != null) {
	        for (String world : worlds) {
	            if (world.equalsIgnoreCase(player.getWorld().getName()))
	                return;
	        }
	    }
	}
}
