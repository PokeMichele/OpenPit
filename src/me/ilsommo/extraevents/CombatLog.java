package me.ilsommo.extraevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.Methods;

public class CombatLog implements Listener {

	private FileConfiguration config = ThePit.getPlugin(ThePit.class).getConfig();
	private ThePit main;
	private Methods methods;
	
	public CombatLog(ThePit main) {
		this.main = main;
		this.methods = main.getMethods();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			methods.combatlog.add((Player) e.getEntity());
		}
		methods.combatlog.add((Player) e.getDamager());

		new BukkitRunnable() {
			
		@Override
		public void run() {
		methods.combatlog.remove(e.getDamager());
		cancel();
			}
		}.runTaskLater(main, config.getLong("CombatTag.Duration")*20);
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity().getPlayer();
		methods.data.set("playerData" + "." + p.getUniqueId() + ".Kills", methods.data.get("playerData." + p.getUniqueId() + ".Kills" + 1 ));
		methods.data.save();
	}
	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		if (methods.combatlog.contains(p)) {
			methods.data.set("playerData" + "." + p.getUniqueId() + ".Logs", methods.data.get("playerData." + p.getUniqueId() + ".Logs" + 1));
			methods.data.save();
		}
	}
}
