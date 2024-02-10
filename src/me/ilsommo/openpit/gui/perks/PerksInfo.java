package me.ilsommo.openpit.gui.perks;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.ilsommo.openpit.ThePit;
import me.ilsommo.openpit.utils.XSounds;
import me.ilsommo.openpit.utils.XTags;
import net.md_5.bungee.api.ChatColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerksInfo implements Listener {

    private ThePit main;

    public PerksInfo(ThePit main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        removePerks(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePerks(event.getPlayer());
    }
    
    private void removePerks(Player player) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(main.perksFile);
        List<String> availablePerks = new ArrayList<>(config.getKeys(false));

        for (String perk : availablePerks) {
            if (config.getStringList(perk).contains(player.getName())) {
                List<String> playersWithPerk = config.getStringList(perk);
                List<ItemStack> itemsToRemove = new ArrayList<>();

                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && isSamePerkItem(item, perk)) {
                        itemsToRemove.add(item);
                    }
                }

                for (ItemStack item : itemsToRemove) {
                    player.getInventory().remove(item);
                }

                playersWithPerk.remove(player.getName());
                config.set(perk, playersWithPerk);
            }
        }

        try {
            config.save(main.perksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isSamePerkItem(ItemStack item, String perk) {
        Material material = getPerkItemMaterial(perk);

        return item.getType() == material;
    }

    private Material getPerkItemMaterial(String perk) {
        switch (perk.toLowerCase()) {
            case "fishing rod":
                return Material.FISHING_ROD;
            case "golden head":
                return Material.GOLDEN_APPLE;
            case "lava bucket":
                return Material.LAVA_BUCKET;
            case "strength-chaining":
                return Material.POTION;
            default:
                return Material.AIR;
        }
    }
    
    private void givePerks(Player player) {
    	FileConfiguration config = YamlConfiguration.loadConfiguration(main.perksFile);
        List<String> availablePerks = new ArrayList<>(config.getKeys(false));

        // Remove perks already assigned to the player and ensure the player is in at most 3 lists
        for (String perk : availablePerks) {
            List<String> playersWithPerk = config.getStringList(perk);
            if (playersWithPerk.contains(player.getName())) {
                playersWithPerk.remove(player.getName());
                config.set(perk, playersWithPerk); // Update the configuration
            }
        }

        int perksGiven = 0;
        // Assign perks to the player
        for (String perk : availablePerks) {
            List<String> playersWithPerk = config.getStringList(perk);
            if (playersWithPerk.size() < 3 && !playersWithPerk.contains(player.getName())) {
                playersWithPerk.add(player.getName());
                config.set(perk, playersWithPerk); // Update the configuration
                givePerkItem(player, perk);
                perksGiven++;
                if (perksGiven >= 3) {
                    break; // Stop assigning perks once 3 perks are given
                }
            }
        }

        try {
            config.save(main.perksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void givePerkItem(Player player, String perkType) {
        Material material;

        switch (perkType.toLowerCase()) {
            case "fishing rod":
                material = Material.FISHING_ROD;
                break;
            case "golden head":
                material = Material.GOLDEN_APPLE;
                break;
            case "lava bucket":
                material = Material.LAVA_BUCKET;
                break;
            case "strength-chaining":
                material = Material.POTION; // Adjust this to the correct material for strength
                break;
            default:
                // If the perk type doesn't match any of the cases above, assign a default material
                material = Material.DIAMOND_SWORD;
                break;
        }

        ItemStack perkItem = new ItemStack(material);

        // Check if player already has the perk item in inventory
        if (!player.getInventory().contains(material)) {
            player.getInventory().addItem(perkItem);
        } else {
            // Inform the player that they already have the item
            player.sendMessage(ChatColor.RED + "You already have this perk item in your inventory!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	FileConfiguration config = YamlConfiguration.loadConfiguration(main.perksFile);
    	
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String invName = event.getInventory().getName();

        // Verifica se il giocatore ha aperto l'inventario "Perks"
        if (invName.equals("Perks")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }

            String perkType = (String) XTags.getItemTag(clickedItem, "Type");
            if (perkType == null) {
                return;
            }

            switch (perkType) {
                case "Go Back":
                    player.closeInventory();
                    main.getPerks().openInventory(player);
                    break;
                case "Fishing Rod":
                case "Golden Head":
                case "Lava Bucket":
                case "Strength-Chaining":
                    handlePerkSelection(player, perkType, config, ChatColor.GREEN + "You equipped the " + perkType + " perk");
                    break;
                case "LOCKED!":
                    player.sendMessage(ChatColor.RED + "This perk is locked! Please upgrade your level to gain access.");
                    player.playSound(player.getLocation(), XSounds.ENDERMAN_HIT.parseSound(), 1, 1);
                    player.closeInventory();
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "PERK NOT FOUND! ERROR");
                    break;
            }
        }
    }

    private void handlePerkSelection(Player player, String perkType, FileConfiguration config, String successMessage) {
        List<String> playersWithPerk = config.getStringList(perkType);

        if (!playersWithPerk.contains(player.getName())) {
            playersWithPerk.add(player.getName());
            config.set(perkType, playersWithPerk);
            givePerkItem(player, perkType);
            player.sendMessage(successMessage);

            try {
                config.save(main.perksFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
