package me.ilsommo.extraevents;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.packets.PacketUtil;
import net.md_5.bungee.api.ChatColor;

public class GoldenHeads implements Listener {

	private ThePit main;
	private FileConfiguration config;
	private PacketUtil packets;
	
	public GoldenHeads(ThePit main) {
		this.main = main;
		this.packets = main.getPackets();
		this.config = main.getConfig();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		@SuppressWarnings("deprecation")
		ItemStack i = p.getItemInHand();
	    if (e.getAction() == null) {
			return;
	    }
	    if (i.getItemMeta() == null) return;
	    if (i.getItemMeta().getDisplayName() == null) return;
	    if (!ChatColor.stripColor(i.getItemMeta().getDisplayName()).equalsIgnoreCase("Golden Head (Right Click)")) return;
	    i.setAmount(i.getAmount() - 1);
		List<String> potions = config.getStringList("PotionEffects-Goldenheads");
		//    for (String s: potions) {
		//        PotionEffectType effect;
		//        try {
		//            effect = PotionEffectType.getByName(s);
		//        } catch (Exception ex) {
		//            ex.printStackTrace();
		//            break;
		//        }
		//        p.addPotionEffect(new PotionEffect(effect, 100, 1));
		//	}
	    }
}
