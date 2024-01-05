package me.ilsommo.openpit.gui.shop;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.Methods;
import me.ilsommo.openpit.utils.XMaterial;
import me.ilsommo.openpit.utils.XTags;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop implements Listener, CommandExecutor {
	ItemStack Obsidian = new ItemStack(Material.OBSIDIAN, 8);
	ItemStack DIAMONDSWORD = new ItemStack(Material.DIAMOND_SWORD, 1);
	ItemStack DiamondChest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
	ItemStack DiamondLeggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
	private Inventory inv;
	private ThePit main;
	private Methods econ;
	FileConfiguration config;
	
	public Shop(ThePit main) {
	    this.main = main;
	    this.config = main.guis;
	    this.econ = main.getMethods();
	    Bukkit.getPluginManager().registerEvents(this, main);
	}

	public void initializeItems(Player p) {
		
		  inv = Bukkit.createInventory(p, 27, "Shop");

		  if (main.getConfig().getStringList("GUI.Shop") == null) return; 
		  
		  for (String key : config.getConfigurationSection("GUI.Shop").getKeys(false)) {
			     ArrayList<String> lore = new ArrayList<String>();
			     for (String s : config.getStringList("GUI.Shop." + key + ".Lore")) {
			    	 lore.add(ChatColor.translateAlternateColorCodes('&', s));
			     }
			  ItemStack i = main.getMethods().createGuiItem(ChatColor.translateAlternateColorCodes('&', config.getString("GUI.Shop." + key + ".Name")),
					  lore, XMaterial.valueOf(config.getString("GUI.Shop." + key + ".Material")).parseItem());
			  inv.setItem(config.getInt("GUI.Shop." + key + ".Slot"), XTags.setItemTag(i, config.getString("GUI.Shop." + key + ".Cost"), "Cost"));
		  }
		  
		  p.openInventory(inv);
	  }

	public void openInventory(Player p) {
	       initializeItems(p);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		e.setCancelled(true);

		Player p = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null) {
			return;
		}
		if (!clickedItem.hasItemMeta()) {
			return;
		}
		ItemMeta meta = clickedItem.getItemMeta();
		if (!meta.hasDisplayName()) {
			return;
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Obsidian")) {
			if (econ.getGold(p) >= 50) {
				p.sendMessage(ChatColor.GREEN + "You bought obsidian");
				econ.removeGold(p, 50);
				p.getPlayer().getInventory().addItem(new ItemStack(Material.OBSIDIAN, 8));
				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Diamond Chestplate")) {
			if (econ.getGold(p) >= 500) {
				p.sendMessage(ChatColor.GREEN + "You bought a diamond chestplate");
				econ.removeGold(p, 500);
				p.getPlayer().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));

				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Pearl")) {
			if (econ.getGold(p) >= main.getConfig().getInt("Pearl")) {
				p.sendMessage(ChatColor.GREEN + "You bought an enderpearl");
				econ.removeGold(p, main.getConfig().getInt("Pearl"));
				p.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Golden Apple")) {
			if (econ.getGold(p) >= 100) {
				p.sendMessage(ChatColor.GREEN + "You bought a golden apple");
				econ.removeGold(p, 100);
				p.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));

				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Diamond Sword")) {
			if (econ.getGold(p) >= 150) {
				p.sendMessage(ChatColor.GREEN + "You bought a diamond sword");
				econ.removeGold(p, 150);
				// Ottieni l'inventario del giocatore
		        ItemStack[] inventory = p.getInventory().getContents();
		        // Scansiona gli slot dell'inventario
		        for (int i = 0; i < inventory.length; i++) {
		            ItemStack item = inventory[i];
		            if (item != null && item.getType() == Material.DIAMOND_SWORD) {
		                return;
		            } else if (item != null && item.getType().name().endsWith("_SWORD")) {
		                p.getInventory().setItem(i, new ItemStack(Material.DIAMOND_SWORD, 1));
		                return; 
		            }
		        }
		        // Se non è stata trovata una spada diversa, aggiungi la "Diamond Sword" nell'inventario (solo se ci sono slot disponibili)
		        for (int i = 0; i < inventory.length; i++) {
		            if (inventory[i] == null) {
		                p.getInventory().setItem(i, new ItemStack(Material.DIAMOND_SWORD, 1));
		                return; 
		            }
		        }

				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
		if (meta.getDisplayName().equals(ChatColor.YELLOW + "Diamond Boots")) {
			if (econ.getGold(p) >= 300) {
				p.sendMessage(ChatColor.GREEN + "You bought diamond boots");
				econ.removeGold(p, 300);
				p.getPlayer().getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));

				return;
			}
			p.sendMessage(ChatColor.RED + "Not enough money");
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
