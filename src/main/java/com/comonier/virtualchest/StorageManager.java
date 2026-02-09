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
        // CORREÇÃO: Cria a pasta 'data' se ela não existir
        File folder = new File(plugin.getDataFolder(), "data");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File f = new File(folder, uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        
        config.set("chests." + chestId, inv.getContents());
        try {
            config.save(f);
        } catch (IOException e) {
            plugin.getLogger().severe("Nao foi possivel salvar o bau de " + uuid);
            e.printStackTrace();
        }
    }

    // Carrega os itens do arquivo para o inventário
    public void loadChest(String uuid, String chestId, Inventory inv) {
        File f = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!f.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        
        // CORREÇÃO: Verificação mais segura para carregar itens na 1.21.1
        if (config.contains("chests." + chestId)) {
            List<?> list = config.getList("chests." + chestId);
            if (list != null) {
                ItemStack[] items = list.toArray(new ItemStack[0]);
                inv.setContents(items);
            }
        }
    }
}
