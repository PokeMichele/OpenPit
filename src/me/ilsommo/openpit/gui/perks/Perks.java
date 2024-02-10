package me.ilsommo.openpit.gui.perks;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.XMaterial;
import me.ilsommo.openpit.utils.XTags;

public class Perks implements Listener, CommandExecutor {
	ArrayList<Player> rod = new ArrayList<Player>();
	ArrayList<Player> goldenhead = new ArrayList<Player>();
	ArrayList<Player> extrahp = new ArrayList<Player>();
	private Inventory inv;
	ArrayList<Player> strength = new ArrayList<Player>();
	ArrayList<Player> goldpickup = new ArrayList<Player>();
	ArrayList<Player> regen = new ArrayList<Player>();
	ArrayList<Player> lava = new ArrayList<Player>();

    private ThePit main;
    private FileConfiguration config;
    private Integer slot;

    public Perks(ThePit main) {
        this.main = main;
        this.config = main.guis;
    }

    public Perks(ThePit main, int i) {
        this.slot = i;
        this.main = main;
        this.config = main.guis;
    }

    public void initializeItems(Player p)
    {
  	  inv = Bukkit.createInventory(p, 27, "Perks");

  	  if (config.getStringList("GUI.Perks.") == null) return; 
  	  
  	  for (String key : config.getConfigurationSection("GUI.Perks").getKeys(false)) {
  		     ArrayList<String> lore = new ArrayList<String>();
  		     for (String s : config.getStringList("GUI.Perks." + key + ".Lore")) {
  		    	 lore.add(ChatColor.translateAlternateColorCodes('&', s));
  		     }
  		  ItemStack i = main.getMethods().createGuiItem(main.getMethods().place(config.getString("GUI.Perks." + key + ".Name"), p), 
  				  lore, Material.valueOf(config.getString("GUI.Perks." + key + ".Material")));

            inv.setItem(config.getInt("GUI.Perks." + key + ".Slot"), XTags.setItemTag(i, config.getString("GUI.Perks." + key + ".Perk-Type"), "Type"));
            if (!p.hasPermission("GUI.Perks." + key + ".Permission")) {
                inv.setItem(config.getInt("GUI.Perks." + key + ".Slot"), main.getMethods().createGuiItem("LOCKED PERK", new ArrayList<>(), Material.DIRT));
            }
        }
        p.openInventory(inv);
    }

    public void openInventory(Player p) {
        initializeItems(p);
    }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    Player player = (Player)sender;
    if (commandLabel.equalsIgnoreCase("perks")) {
      openInventory(player);
    }
    return false;
  }
  
/*  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
	  if (!(e.getEntity() instanceof Player)) return;
		Player p = e.getEntity();
		if (p.getKiller() != null) {
            Player killer = p.getKiller();
            if (strength.contains(killer)) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7 * 20, 0), true);
      		}
        }
  }*/
 
  private void giveStandardItems(Player p) {
	  
	  p.getInventory().setLeggings(XMaterial.CHAINMAIL_LEGGINGS.parseItem());
	  p.getInventory().setChestplate(XMaterial.CHAINMAIL_CHESTPLATE.parseItem());
	  p.getInventory().setBoots(XMaterial.CHAINMAIL_BOOTS.parseItem());
	  p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
	  p.getInventory().setItem(1, new ItemStack(Material.BOW));
	  p.getInventory().setItem(2, new ItemStack(Material.ARROW, 32));
	  
	  /*giveItems(p);*/
	  p.updateInventory();

  }
  @EventHandler
  public void on(PlayerRespawnEvent e) {
	  new BukkitRunnable() {
		
		@Override
		public void run() {
			if (e.getPlayer().getInventory().contains(Material.IRON_SWORD)){
				return;
			}
			  giveStandardItems(e.getPlayer().getPlayer());
			
		}
	}.runTaskLater(main, 20L);
  }
  @EventHandler
  public void one(PlayerJoinEvent e) {
	  new BukkitRunnable() {
		@Override
		public void run() {
			  giveStandardItems(e.getPlayer());
		}
	}.runTaskLater(main, 20L);
  }
}
