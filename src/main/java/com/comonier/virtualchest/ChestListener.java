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
        String title = event.getView().getTitle();
        
        // Verificamos se o título contém o caractere '#' que usamos para identificar nossos baús
        if (title.contains("#")) {
            try {
                // Extraímos apenas os números do título para saber qual ID de baú salvar
                // Exemplo: "§aBaú #1 aberto!" -> extrai "1"
                String chestId = title.replaceAll("[^0-9]", "");
                
                if (!chestId.isEmpty()) {
                    Inventory inv = event.getInventory();
                    String uuid = event.getPlayer().getUniqueId().toString();
                    
                    // Chama o salvamento (que rodará em Async via StorageManager)
                    storage.saveChest(uuid, chestId, inv);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Erro ao processar fechamento de bau: " + e.getMessage());
            }
        }
    }
}
