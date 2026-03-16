package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        // Proteção para menus fixos de seleção (Exemplo: se você criar um menu "Seus Baús")
        // No Folia, cancelamentos de cliques de inventário funcionam normalmente na thread da região.
        if (title.equalsIgnoreCase("§6Seus Baús") || title.equalsIgnoreCase("§6Your Chests")) {
            event.setCancelled(true); // Impede que o jogador retire o item do menu
        }
    }
}
