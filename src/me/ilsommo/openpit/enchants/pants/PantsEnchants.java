package me.ilsommo.openpit.enchants.pants;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.XTags;

public class PantsEnchants implements Listener {
	private ThePit main;
	public HashMap<UUID, Boolean> Booboo = new HashMap<>();
	public HashMap<UUID, Boolean> Creative = new HashMap<>();
	private HashMap<UUID, ItemStack> pants = new HashMap<>();
	private HashMap<UUID, Integer> slot = new HashMap<>();
	private HashMap<UUID, ItemStack> respawnleggings = new HashMap<>();

	public PantsEnchants(ThePit main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
		
		new Creative(main);
		new Booboo(main);
		new Cricket(main);
		new DangerClose(main);
		new Eggs(main);
		new Electrolytes(main);
		new Hearts(main);
		new TNT(main);
		new StrikeGold(main);
		
		//Rare Enchants
		new DoubleJump(main);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Pants Enchantments Registered.");
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (!checkNbt(e.getEntity().getInventory().getLeggings())) {
			return;
		}
		ItemStack i = e.getEntity().getInventory().getLeggings();
		ItemMeta s = i.getItemMeta();
        List<String> lore = i.getItemMeta().getLore();
		lore.set(0, ChatColor.GREEN + String.valueOf((Integer) XTags.getItemTag(i, "Lives") - 1) + ChatColor.GRAY + "/" + String.valueOf(XTags.getItemTag(i, "MaxLives")));
		s.setLore(lore);
		i.setItemMeta(s);

		if ((Integer) XTags.getItemTag(e.getEntity().getInventory().getLeggings(), "Lives") <= 0) {
			e.getEntity().sendMessage(ChatColor.RED + "Leggings lost.");
			e.getEntity().playSound(e.getEntity().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1F, 1F);
			return;
		}
		respawnleggings.put(e.getEntity().getUniqueId(),  XTags.setItemTag(
	    		   i,
	    		  (Integer) XTags.getItemTag(
	    				   e.getEntity().getInventory().getLeggings(), 
	    				   "Lives") - 1, 
	    		  
	    		   "Lives"));
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (respawnleggings.get(e.getPlayer().getUniqueId()) == null) return;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				e.getPlayer().getInventory().setLeggings(respawnleggings.get(e.getPlayer().getUniqueId()));
				respawnleggings.remove(e.getPlayer().getUniqueId());
			}
		}.runTaskLater(main, 20L);
	}
	public static boolean checkNbt(ItemStack i) {
		if (i == null) {
			return false;
		}
		if (XTags.getItemTag(i) == null) {
			return false;
		}
		if (!i.hasItemMeta()) {
			return false;
		}
		return true;
	}
	public static boolean check(ItemStack i, String enchant) {
		if (!checkNbt(i)) {
			return false;
		}
		
		if (XTags.getItemTag(i, enchant) == null) {
			return false;
		}
		if (XTags.getItemTag(i, enchant).equals("true")) {
			return true;
		}
		return false;
	}
	
}
