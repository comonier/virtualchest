package com.comonier.virtualchest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final Main plugin;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
    }

    public void saveChest(String uuid, String chestId, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        Map<Integer, ItemStack> itemsToSave = new HashMap<>();
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && !item.getType().isAir()) {
                itemsToSave.put(i, item);
            }
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            File folder = new File(plugin.getDataFolder(), "data");
            if (!folder.exists()) folder.mkdirs();

            File f = new File(folder, uuid + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(f);
            
            config.set("chests." + chestId, null); 
            if (!itemsToSave.isEmpty()) {
                config.set("chests." + chestId, itemsToSave);
            }

            try {
                config.save(f);
            } catch (IOException e) {
                // Puxa a mensagem traduzida e substitui os placeholders
                String errorMsg = plugin.getMsg("save_error")
                        .replace("%player%", uuid)
                        .replace("%id%", chestId);
                plugin.getLogger().severe(errorMsg);
            }
        });
    }

    public void loadChest(String uuid, String chestId, Inventory inv) {
        File f = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!f.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection section = config.getConfigurationSection("chests." + chestId);
        
        if (section != null) {
            inv.clear();
            for (String key : section.getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    ItemStack item = section.getItemStack(key);
                    if (slot < inv.getSize()) inv.setItem(slot, item);
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
