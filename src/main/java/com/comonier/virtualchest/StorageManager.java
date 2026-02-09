package com.comonier.virtualchest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class StorageManager {
    private final Main plugin;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
    }

    // Salva os itens do inventário em um arquivo .yml
    public void saveChest(String uuid, String chestId, Inventory inv) {
        File f = new File(plugin.getDataFolder() + "/data/" + uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        
        config.set("chests." + chestId, inv.getContents());
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carrega os itens do arquivo para o inventário
    public void loadChest(String uuid, String chestId, Inventory inv) {
        File f = new File(plugin.getDataFolder() + "/data/" + uuid + ".yml");
        if (!f.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        List<?> items = config.getList("chests." + chestId);
        
        if (items != null) {
            ItemStack[] content = items.toArray(new ItemStack[0]);
            inv.setContents(content);
        }
    }
}
