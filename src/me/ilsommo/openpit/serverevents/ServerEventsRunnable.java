package me.ilsommo.openpit.serverevents;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.ilsommo.openpit.ThePit;

public class ServerEventsRunnable extends BukkitRunnable {

    private ThePit main;

    public ServerEventsRunnable(ThePit main) {
        this.main = main;
    }

    @Override
    public void run() {
        String event = randomEvent();
        if (event != null) {
            String formattedEvent = formatEventMessage(event);
            Bukkit.broadcastMessage(formattedEvent);
        }
    }

    private String randomEvent() {
        List<String> events = main.getConfig().getStringList("Events");
        if (events.isEmpty()) {
            return null;
        }
        int pick = new Random().nextInt(events.size());
        return events.get(pick);
    }

    private String formatEventMessage(String event) {
        // Personalizza il formato del messaggio dell'evento qui
        return "Evento: " + event;
    }
}
