package me.ilsommo.extraevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.packets.PacketUtil;
import me.ilsommo.openpit.utils.Messages;
import me.ilsommo.openpit.utils.XTags;

public class Streaks implements Listener {
	
	private Map<String, Integer> killstreak = new HashMap<String, Integer>();
	private ThePit main;
	private Messages messages;
	private FileConfiguration config;
	private PacketUtil packets;
	
	public Streaks(ThePit main) {
		this.main = main;
		this.config = main.getConfig();
		this.packets = main.getPackets();
		this.messages = main.getMessages();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	public Map<String, Integer> getkillstreak() {
		return killstreak;
	}
}