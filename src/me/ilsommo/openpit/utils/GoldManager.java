package me.ilsommo.openpit.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ilsommo.openpit.ThePit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GoldManager {
    private final FileConfiguration dataConfig;
    private final File dataFile;
    private final Map<UUID, Integer> goldMap;

    public GoldManager() {
        goldMap = new HashMap<>();
        dataFile = new File(ThePit.getInstance().getDataFolder(), "gold.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadData();
    }

    private void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            int goldAmount = dataConfig.getInt(key);
            goldMap.put(playerUUID, goldAmount);
        }
    }

    public int getGold(UUID playerUUID) {
        return goldMap.getOrDefault(playerUUID, 0);
    }

    public void addGold(UUID playerUUID, int amount) {
        int currentGold = goldMap.getOrDefault(playerUUID, 0);
        goldMap.put(playerUUID, currentGold + amount);
        saveData();
    }

    public void saveData() {
        for (Map.Entry<UUID, Integer> entry : goldMap.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
