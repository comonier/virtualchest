package com.comonier.virtualchest;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ChestHolder implements InventoryHolder {
    private final String chestId;
    private final String ownerUUID;

    public ChestHolder(String ownerUUID, String chestId) {
        this.ownerUUID = ownerUUID;
        this.chestId = chestId;
    }

    public String getChestId() {
        return chestId;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null; // Não necessário para esta implementação
    }
}
