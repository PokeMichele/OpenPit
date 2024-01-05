package me.ilsommo.openpit.gui.perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
import me.ilsommo.openpit.utils.XSounds;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        String invName = e.getInventory().getName();
        if (!invName.equals("Perks")) {
            return;
        }
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String perkType = (String) XTags.getItemTag(clickedItem, "Type");
        if (perkType == null) {
            return;
        }

        switch (perkType) {
            case "Go Back":
                p.closeInventory();
                main.getPerks().openInventory(p);
                break;
            case "Fishing Rod":
                handlePerkSelection(p, "FISHING_ROD", rod, ChatColor.GREEN + "You equipped the Fishing Rod perk");
                break;
            case "Golden Head":
                handlePerkSelection(p, "GOLDEN_APPLE", goldenhead, ChatColor.GREEN + "You equipped GoldenHeads perk");
                break;
            case "Lava Bucket":
                handlePerkSelection(p, "LAVA_BUCKET", lava, ChatColor.GREEN + "You equipped Lava Bucket perk");
                break;
            case "Strength-Chaining":
                handlePerkSelection(p, "REDSTONE", strength, ChatColor.GREEN + "You equipped Strength Chaining perk");
                break;
            case "LOCKED!":
                p.sendMessage(ChatColor.RED + "This perk is locked! Please upgrade your level to gain access.");
                p.playSound(p.getLocation(), XSounds.ENDERMAN_HIT.parseSound(), 1, 1);
                p.closeInventory();
                break;
            default:
                p.sendMessage(ChatColor.RED + "PERK NOT FOUND! ERROR");
                break;
        }
    }

    private void handlePerkSelection(Player player, String perkData, List<Player> perkList, String successMessage) {
        HashMap<Integer, String> data = new HashMap<>();
        data.put(slot, perkData);

        rod.remove(player);
        goldenhead.remove(player);
        lava.remove(player);
        strength.remove(player);

        perkList.add(player);
        data.put(main.getMethods().dataSlot.get(player.getUniqueId()), perkData);
        main.getMethods().perk1.put(player, data);
        giveItems(player);
        player.sendMessage(successMessage);
    }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    Player player = (Player)sender;
    if (commandLabel.equalsIgnoreCase("perks")) {
      openInventory(player);
    }
    return false;
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
	  if (!(e.getEntity() instanceof Player)) return;
		Player p = e.getEntity();
		if (p.getKiller() != null) {
            Player killer = p.getKiller();
            if (strength.contains(killer)) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7 * 20, 0), true);
      		}
        }
  }
  public void removeExceptLava(Player p) {
	  rod.remove(p);
	  goldenhead.remove(p);
	  extrahp.remove(p);
	  strength.remove(p);
	  goldpickup.remove(p);
	  regen.remove(p);
  }
  public void removeExceptRod(Player p) {
	  lava.remove(p);
	  goldenhead.remove(p);
	  extrahp.remove(p);
	  strength.remove(p);
	  goldpickup.remove(p);
	  regen.remove(p);
  }
  public void removeExceptStrength(Player p) {
	  rod.remove(p);
	  lava.remove(p);
	  goldenhead.remove(p);
	  extrahp.remove(p);
	  goldpickup.remove(p);
	  regen.remove(p);
  }
  public void removeExceptHead(Player p) {
	  rod.remove(p);
	  lava.remove(p);
	  strength.remove(p);
	  extrahp.remove(p);
	  goldpickup.remove(p);
	  regen.remove(p);
  }
  public void giveItems(Player p) {
	  if (goldenhead.contains(p)) {
	  }
	  if (rod.contains(p)) {
		  p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
		  p.updateInventory();
	  }
	  if (lava.contains(p)) {
		  p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
		  p.updateInventory();
	  }
	  System.out.println("Perks Items Given to " + p.getName());
  }
  private void giveStandardItems(Player p) {
	  
	  p.getInventory().setLeggings(XMaterial.CHAINMAIL_LEGGINGS.parseItem());
	  p.getInventory().setChestplate(XMaterial.CHAINMAIL_CHESTPLATE.parseItem());
	  p.getInventory().setBoots(XMaterial.CHAINMAIL_BOOTS.parseItem());
	  p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
	  p.getInventory().setItem(1, new ItemStack(Material.BOW));
	  p.getInventory().setItem(2, new ItemStack(Material.ARROW, 32));
	  
	  giveItems(p);
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
