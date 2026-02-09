package com.comonier.virtualchest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        // Exemplo: Se você criar um menu chamado "Seus Baús", este código impede o roubo de itens do menu
        if (title.equalsIgnoreCase("§6Seus Baús") || title.equalsIgnoreCase("§6Your Chests")) {
            event.setCancelled(true); // Cancela o clique (o item não sai do lugar)
            // Aqui você poderia adicionar: if (event.getRawSlot() == 10) player.performCommand("pv 1");
        }
    }
}
