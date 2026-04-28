package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestListener implements Listener {
    private final StorageManager storage;

    public ChestListener(Main plugin, StorageManager storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        
        if (inv.getHolder() instanceof ChestHolder holder) {
            String uuid = holder.getOwnerUUID();
            String id = holder.getChestId();
            
            storage.saveChest(uuid, id, inv);
        }
    }
}
