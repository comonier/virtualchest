package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestListener implements Listener {
    private final Main plugin;
    private final StorageManager storage;

    public ChestListener(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        
        // Verificação segura: O inventário possui o nosso Holder personalizado?
        // Isso impede que menus de outros plugins (como PlayerParticles) sejam salvos por erro.
        if (inv.getHolder() instanceof ChestHolder holder) {
            String uuid = holder.getOwnerUUID();
            String id = holder.getChestId();
            
            // Salva o conteúdo no banco de dados (MySQL ou SQLite)
            storage.saveChest(uuid, id, inv);
        }
    }
}
