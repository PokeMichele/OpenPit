package me.ilsommo.extraevents;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.XMaterial;
import me.ilsommo.openpit.utils.XSounds;
import net.md_5.bungee.api.ChatColor;

public class BlockedEvents implements Listener {

	public ArrayList<Player> bypass = new ArrayList<Player>();
	private ThePit main;
	  
	public BlockedEvents(ThePit main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		for (String s : main.getConfig().getStringList("Blocked-Worlds")) {
			if (Bukkit.getWorld(s) == null) break;
			if (Bukkit.getWorld(s).equals(p.getWorld())) {
				return;
			}
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)){
			p.sendMessage(ChatColor.RED + "BYPASS MODE");
		}
		if (p.getGameMode().equals(GameMode.SURVIVAL)){
			e.setCancelled(true);
		}

	}
	@EventHandler
	public void onCollision(VehicleEntityCollisionEvent evt){
	evt.setCancelled(true);
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		for (String s : main.getConfig().getStringList("Blocked-Worlds")) {
			if (Bukkit.getWorld(s) == null) break;
			if (Bukkit.getWorld(s).equals(e.getEntity().getWorld())) {
				return;
			}
		}
		if (e.getEntity() instanceof Villager) {
			e.setCancelled(true);
		}
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void manipulate(PlayerArmorStandManipulateEvent e)
	{
		
	        if(!e.getRightClicked().isVisible())
	        {
	            e.setCancelled(true);
	        }
	}
	@EventHandler
	public void rightclick(PlayerInteractAtEntityEvent e) {
		for (String s : main.getConfig().getStringList("Blocked-Worlds")) {
			if (Bukkit.getWorld(s) == null) break;
			if (Bukkit.getWorld(s).equals(e.getPlayer().getWorld())) {
				return;
			}
		}
		if (e.getRightClicked() instanceof Villager || e.getRightClicked() instanceof ArmorStand) {
			if (e.getPlayer().getItemInHand().getType().equals(Material.BEDROCK)) {
				if (!e.getPlayer().hasPermission("thepit.removeent")) {
					return;
				}
				for (Entity ent : e.getRightClicked().getNearbyEntities(1D, 2D, 1D)) {
					ent.remove();
					e.getPlayer().playSound(e.getPlayer().getLocation(), XSounds.NOTE_PLING.parseSound(), .1F, 1F);
				}
				e.getPlayer().sendMessage("removed");
			}
		}
	}
	
	@EventHandler
	public void A(BlockPlaceEvent e) {
		
		Player p = e.getPlayer();
		for (String s : main.getConfig().getStringList("Blocked-Worlds")) {
			if (Bukkit.getWorld(s) == null) break;
			if (Bukkit.getWorld(s).equals(p.getWorld())) {
				return;
			}
		}
		Material t = e.getBlock().getType();
		if (!p.hasPermission("thepit.placeitems")) {
			e.setCancelled(true);
			return;
		}
			if (e.getBlock().getType().equals(XMaterial.ENDER_CHEST.parseMaterial())) {
				Location loc = e.getBlock().getLocation();
				Location loc2 = e.getBlock().getLocation();
				p.sendMessage(ChatColor.GREEN + "BLOCK PLACED");
				return;
		}
			if (e.getBlock().getType().equals(XMaterial.ENCHANTING_TABLE.parseMaterial())) {
				Location loc = e.getBlock().getLocation();
				Location loc2 = e.getBlock().getLocation();
				p.sendMessage(ChatColor.GREEN + "BLOCK PLACED");
				return;
		}
		if (t == XMaterial.OBSIDIAN.parseMaterial() || t == XMaterial.COBBLESTONE.parseMaterial() || t == XMaterial.LAVA_BUCKET.parseMaterial() || t == XMaterial.OAK_WOOD.parseMaterial() || t == XMaterial.TNT.parseMaterial()) {
			return;
		}
			}
}