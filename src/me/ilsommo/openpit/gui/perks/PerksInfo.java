package me.ilsommo.openpit.gui.perks;

import org.bukkit.Bukkit;
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
        givePerks(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePerks(event.getPlayer());
    }

    private void givePerks(Player player) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(main.perksFile);

        List<String> availablePerks = new ArrayList<>(config.getKeys(false));

        // Rimuovi i perks già presenti nel giocatore
        for (String perk : availablePerks) {
            if (config.getStringList(perk).contains(player.getName())) {
                config.getStringList(perk).remove(player.getName());
            }
        }

        // Assegna al massimo 3 nuovi perks al giocatore
        for (String perk : availablePerks) {
            List<String> playersWithPerk = config.getStringList(perk);
            if (playersWithPerk.size() < 3) {
                playersWithPerk.add(player.getName());
                config.set(perk, playersWithPerk);
                givePerkItem(player, perk);
            }
        }

        try {
            config.save(main.perksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void givePerkItem(Player player, String perk) {
        // Aggiungi qui il codice per assegnare l'oggetto corrispondente al perk
        // Ad esempio, puoi dare un'istanza di ItemStack con il materiale desiderato

        Material material;

        switch (perk.toLowerCase()) {
            case "rod":
                material = Material.FISHING_ROD;
                break;
            case "goldenhead":
                material = Material.GOLDEN_APPLE;
                break;
            case "lava":
                material = Material.LAVA_BUCKET;
                break;
            case "strength":
                material = Material.POTION; // Puoi impostare il materiale corretto per la forza
                break;
            default:
                // Se il perk non corrisponde a nessuno dei casi sopra, assegna un materiale di default
                material = Material.DIAMOND_SWORD;
                break;
        }

        ItemStack perkItem = new ItemStack(material);
        player.getInventory().addItem(perkItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
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

            FileConfiguration config = YamlConfiguration.loadConfiguration(main.perksFile);

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
