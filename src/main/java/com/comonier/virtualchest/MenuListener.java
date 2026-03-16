package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        // Caso você crie um menu de seleção de baús no futuro
        if (title.contains("Baús") || title.contains("Chests")) {
            // Se for um menu decorativo/estático, cancela o clique
            // event.setCancelled(true); 
        }
    }
}
