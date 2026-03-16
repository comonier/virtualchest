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
        // Verifica o caractere '#' que define nossos baús no PVCommand
        if (title.contains("#")) {
            String chestId = title.replaceAll("[^0-9]", "");
            if (!chestId.isEmpty()) {
                storage.saveChest(event.getPlayer().getUniqueId().toString(), chestId, event.getInventory());
            }
        }
    }
}
