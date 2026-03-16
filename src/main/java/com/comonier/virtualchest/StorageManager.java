package com.comonier.virtualchest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class StorageManager {
    private final Main plugin;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
    }

    public void saveChest(String uuid, String chestId, Inventory inv) {
        // Criamos um mapa para salvar apenas [Slot -> Item]
        Map<Integer, ItemStack> itemsToSave = new HashMap<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && !item.getType().isAir()) {
                itemsToSave.put(i, item);
            }
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            File folder = new File(plugin.getDataFolder(), "data");
            if (!folder.exists()) folder.mkdirs();

            File f = new File(folder, uuid + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(f);
            
            // Limpa o baú anterior para não sobrar lixo e salva o novo mapa
            config.set("chests." + chestId, null); 
            if (!itemsToSave.isEmpty()) {
                config.set("chests." + chestId, itemsToSave);
            }

            try {
                config.save(f);
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao salvar bau de " + uuid);
            }
        });
    }

    public void loadChest(String uuid, String chestId, Inventory inv) {
        File f = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!f.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection section = config.getConfigurationSection("chests." + chestId);
        
        if (section != null) {
            inv.clear(); // Garante que o baú comece vazio
            for (String key : section.getKeys(false)) {
                int slot = Integer.parseInt(key);
                ItemStack item = section.getItemStack(key);
                if (slot < inv.getSize()) {
                    inv.setItem(slot, item);
                }
            }
        }
    }
}
