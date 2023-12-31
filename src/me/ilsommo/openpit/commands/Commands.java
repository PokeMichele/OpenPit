package me.ilsommo.openpit.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.user.ZUser;
import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.enchants.enchants.PantEnchantTypes;
import me.ilsommo.openpit.packets.PacketUtil;
import me.ilsommo.openpit.utils.Messages;
import me.ilsommo.openpit.utils.Methods;
import me.ilsommo.openpit.utils.XTags;

public class Commands implements CommandExecutor {
	

	private ThePit main = ThePit.getPlugin(ThePit.class);
	private ArrayList<Entity> near = new ArrayList<Entity>();
	private FileConfiguration config = main.getConfig();
	private Messages messages = main.getMessages();
	private Methods methods = main.getMethods();
	private PacketUtil nms = main.getPackets();
	
	public Commands(ThePit main) {
		// TODO Auto-generated constructor stub
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Not a player!");
			return true;
		}
		Player p = (Player) sender;
		String name = cmd.getName();
		if (name.equalsIgnoreCase("openpit")) {
			switch (args.length) {
			
			case 0:
				for (String s : messages.help)
				sender.sendMessage(methods.place(s, p));
				return true;
			case 1:
				  if (args[0].equalsIgnoreCase("enchant")) {
					  p.sendMessage(ChatColor.RED + "Invalid usgae. Try /openpit enchant <enchantment>");
					  return true;
				  }
		          if (args[0].equalsIgnoreCase("spawnshop")) {
		        	  PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		        	    if (pluginManager.isPluginEnabled("znpcs-4.6") || pluginManager.isPluginEnabled("ServersNPC") == true) {
		        	        int npcid = 118;
		        	        NPC npc = ServersNPC.createNPC(npcid, NPCType.PLAYER, p.getLocation(), "&6&lShop");
		        	        npc.lookAt(ZUser.find(p), p.getLocation(), true);
		        	        npc.getNpcPojo().getClickActions().add(new NPCAction("CMD", "openpit shop"));
		        	        p.sendMessage(ChatColor.GOLD + "Shop NPC Successfully Created");
		        	        return true;
		        	    } else {
		        	        p.sendMessage("Please, install ZNPCs to run this command");
		        	        return true;
		        	    }
                  }
		          if (args[0].equalsIgnoreCase("spawnperks")) {
		        	    PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		        	    if (pluginManager.isPluginEnabled("znpcs-4.6") || pluginManager.isPluginEnabled("ServersNPC") == true) {
		        	        int npcid = 104;
		        	        NPC npc = ServersNPC.createNPC(npcid, NPCType.PLAYER, p.getLocation(), "&1&lPerks");
		        	        npc.lookAt(ZUser.find(p), p.getLocation(), true);
		        	        npc.getNpcPojo().getClickActions().add(new NPCAction("CMD", "openpit perks"));
		        	        p.sendMessage(ChatColor.AQUA + "Perks NPC Successfully Created");
		        	        return true;
		        	    } else {
		        	        p.sendMessage("Please, install ZNPCs to run this command");
		        	        return true;
		        	    }
		        	}
		          if (args[0].equalsIgnoreCase("perks")) {
	                   main.getPerks().openInventory(p);
	                      return true;
	                  }
		          if (args[0].equalsIgnoreCase("shop")) {
	                   main.getShop().openInventory(p);
	                      return true;
	                  }
			         
			    if (args[0].equalsIgnoreCase("giveitem")) {
			    	  if (!p.hasPermission("falconseeker.dev")) {
			    		  return true;
			    	  }
			          ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			          ItemMeta itemMeta = item.getItemMeta();
			          LeatherArmorMeta lch = (LeatherArmorMeta) item.getItemMeta();

			          Random ran = new Random();
			          
			          switch(ran.nextInt(5)) {
			          case 1:
			        	  //RED
				          lch.setColor(Color.fromRGB(255, 0, 0));
				          lch.setDisplayName(ChatColor.RED + "Fresh Pants");
				          break;
			          case 2:
			        	  //GREEN
				          lch.setColor(Color.fromRGB(0, 255, 0));
				          lch.setDisplayName(ChatColor.GREEN + "Fresh Pants");
				          break;
			          case 3:
			        	  //YELLOW
				          lch.setColor(Color.fromRGB(255, 255, 0));
				          lch.setDisplayName(ChatColor.YELLOW + "Fresh Pants");

			          	  break;
			          case 4:
			        	  //ORANGE - TWEAK THIS LATER
				          lch.setColor(Color.fromRGB(255, 155, 0));
				          lch.setDisplayName(ChatColor.GOLD + "Fresh Pants");			          item.setItemMeta(lch);

			          	  break;
			          case 5:
			        	  //BLUE - TWEAK THIS LATER
				          lch.setColor(Color.fromRGB(0, 20, 255));
			          	  lch.setDisplayName(ChatColor.AQUA + "Fresh Pants");
			          	  break;
			          default:
			        	  //BLUE - TWEAK THIS LATER
				          lch.setColor(Color.fromRGB(0, 20, 255));
			          	  lch.setDisplayName(ChatColor.AQUA + "Fresh Pants");
			          	  break; 
			          	  
			          }
			          List<String> l = new ArrayList<String>();
			              l.add(ChatColor.GRAY + "Kept on death.");
			              lch.setLore(l);
			              item.setItemMeta(lch);

			          ItemStack s =  XTags.setItemTag(item, "Tier I", "Tier");
			          ItemStack t =  XTags.setItemTag(s, "true", "Enchantable");

			          p.getInventory().addItem(XTags.setItemTag(t, 1000, "Cost"));

			          p.updateInventory();
			          
			          p.sendMessage(ChatColor.GREEN + "Item Received");
			          return true;
			          }
		        if (args[0].equalsIgnoreCase("setevent")) {
		        	main.getConfig().set("Events.Holo-Location", p.getLocation());
		        	main.saveDefaultConfig();
		        }
				if (args[0].equalsIgnoreCase("reload")) {
					if (!p.hasPermission("falconseeker.reload")) {
						p.sendMessage(methods.place(messages.noperms, p));
						return true;
					}
					p.sendMessage(methods.place(messages.reload, p));
					main.saveDefaultConfig();
					main.reloadConfig();
					return true;
				}
				if (args[0].equalsIgnoreCase("kills")) {
					if (!p.hasPermission("falconseeker.kills")) {
						p.sendMessage(methods.place(messages.noperms, p));
						return true;
					}
					

					HashMap<String, Integer> players = new HashMap<String, Integer>();

					//Filling the hashMap
					for(String playerName: main.getConfig().getConfigurationSection("Top").getKeys(false)){
						players.put(playerName, main.getConfig().getInt("Top." + playerName + ".Kills"));
						}

					sender.sendMessage("===========[Top]===========");
					Location location = p.getLocation();
					methods.createHolo(p);
					methods.createHolo(p);
					String nextTop = "";
					Integer nextTopKills = 0;

		for(int i = 1; i < 11; i++){
			for(String playerName: players.keySet()){
				if(players.get(playerName) > nextTopKills){
					nextTop = playerName;
					nextTopKills = players.get(playerName);
				}
			}
			methods.createHolo(p);
			sender.sendMessage(i + " # " + nextTop + " : " + nextTopKills);
			players.remove(nextTop);
			nextTop = "";
			nextTopKills = 0;
		}
		sender.sendMessage("===========[Top]===========");
					return true;
				}
				if (args[0].equalsIgnoreCase("help")) {
					for (String s : messages.help) {
					p.sendMessage(methods.place(s, p));
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("stats")) {
					for (Entity ent : p.getNearbyEntities(1000, 1000, 1000)) {
						near.add(ent);
						p.sendMessage(String.valueOf(near.size()));
						near.remove(ent);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("goldstart")) {
					List<Location> loc = (List<Location>) config.getList("Gold-Locations");
					if (loc == null) {
						p.sendMessage("LOCATION NULL");
						return true;
					}
					Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
						public void run() {
							List<Location> loc = (List<Location>) config.getList("Gold-Locations");
							int amount = config.getInt("Gold.Amount");
							String mat = config.getString("Gold.Material");
							for (Location location : loc) {
								if (mat.equalsIgnoreCase("GOLD")) {
								    continue;
								}
								location.getWorld().dropItemNaturally(location, new ItemStack(Material.valueOf(mat), amount));
							}
						}
					}, 1L, 20*config.getLong("Gold.Intervals-In-Seconds"));
					return true;
				}
					if ((args[0].equalsIgnoreCase("createholo"))) {
			        main.getMethods().createHolo(p);
			        p.sendMessage(ChatColor.GREEN + "Hologram Created Successfully :)");
				    return true;
			    	}
					if (args[0].equalsIgnoreCase("respawn")) {
						if (args.length < 2) {
							p.teleport(p.getWorld().getSpawnLocation());
							p.sendMessage(methods.place(messages.respawn, p));
							return true;
					}
					if (!p.hasPermission("falconseeker.respawnothers")) {
						p.sendMessage(methods.place(messages.noperms, p));
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						p.sendMessage(ChatColor.RED + "Target is null!");
						return true;
					}
					target.teleport(target.getWorld().getSpawnLocation());
					target.sendMessage(ChatColor.GREEN + p.getName() + " has respawned you!");
					p.sendMessage(ChatColor.GREEN + "You sent " + target.getName() + " to spawn");
					return true;
				}
					else {
						p.sendMessage(methods.place(messages.unknown, p));
						return true;
				}
			case 2:
				if (args[0].equalsIgnoreCase("enchant")) {
					if (p.getInventory().getItemInHand() == null) {
						p.sendMessage(ChatColor.RED + "INVALID ITEM.");
						return true;
					}
					if (p.getInventory().getItemInHand().getItemMeta() == null) {
						p.sendMessage("Invalid item");
						return true;
					}
					if (PantEnchantTypes.valueOf(args[1].toUpperCase()) == null) {
						p.sendMessage("Invalid enchantment name");
						for (PantEnchantTypes s : PantEnchantTypes.values()) {
							p.sendMessage(String.valueOf("s"));
						}
						return true;
					}
				     ItemStack i = main.getMethods().setItemName(p.getInventory().getItemInHand(), 
					p.getInventory().getItemInHand().getItemMeta().getLore()
					, PantEnchantTypes.valueOf(args[1].toUpperCase()), p, false);
				     p.sendMessage("Enchanted");
				     p.getInventory().setItemInHand(i);
					return true;
				}
			default:
		    	if ((args[0].equalsIgnoreCase("createholo"))) {
		        main.getMethods().createHolo(p);
		        p.sendMessage(ChatColor.GREEN + "Hologram Created Successfully :)");
			    return true;
		    	}
		    return true;
			}
		}
			
		return false;
	}

}
