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
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.packets.PacketUtil;
import me.ilsommo.openpit.utils.Messages;

public class InstantDeath implements Listener {
  
	private ThePit main;
	private PacketUtil packets;
	private FileConfiguration config;
	private Messages messages;
	private HashMap<UUID, Integer> map = new HashMap<>();
	
	public InstantDeath(ThePit main) {
		this.main = main;
		this.messages = main.getMessages();
		this.packets = main.getPackets();
		this.config = main.getConfig();
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		
		Player p = e.getEntity();
		map.put(p.getUniqueId(), p.getTotalExperience());
		e.getDrops().clear();
		p.spigot().respawn();
		p.setHealth(20.0D);
		p.setExhaustion(0.0F);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setGameMode(GameMode.SURVIVAL);
				Location spawnPoint = p.getWorld().getSpawnLocation();
				p.teleport(spawnPoint);
				p.setTotalExperience(map.get(p.getUniqueId()));
			}
		}.runTaskLater(main, 1L);
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
	    
	    Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
	        @Override
	        public void run() {
	            player.spigot().respawn();
	            Location respawnLoc = player.getLocation();
	            Bukkit.getPluginManager().callEvent(new PlayerAutoRespawnEvent(player, deathLoc, respawnLoc));
	        }
	    }, 1L);
	}
}
