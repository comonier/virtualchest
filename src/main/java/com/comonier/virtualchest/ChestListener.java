package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ChestListener implements Listener {
    private final Main plugin;
    private final StorageManager storage;

    public ChestListener(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        // Verifica se o inventário fechado é um dos nossos baús virtuais
        if (title.contains("aberto!") || title.contains("opened!")) {
            // Extrai o ID do baú pelo título (um pouco simplista, mas funcional para agora)
            String[] parts = title.split("#");
            if (parts.length > 1) {
                String chestId = parts[1].replace("!", "").trim();
                storage.saveChest(event.getPlayer().getUniqueId().toString(), chestId, event.getInventory());
            }
        }
    }
}
