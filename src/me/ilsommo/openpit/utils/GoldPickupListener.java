package me.ilsommo.openpit.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import me.ilsommo.openpit.ThePit;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class GoldPickupListener implements Listener {
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onGoldPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (item.getType() == Material.GOLD_INGOT) {
            event.setCancelled(true);
            event.getItem().remove();

            int goldAmount = item.getAmount(); // Ottieni la quantità di oro raccolto
            UUID playerUUID = player.getUniqueId();
            ThePit.getInstance().getGoldManager().addGold(playerUUID, goldAmount);
            player.sendMessage(ChatColor.GOLD + "+" + goldAmount + " Gold Ingot(s)"); 
        }
    }
}

