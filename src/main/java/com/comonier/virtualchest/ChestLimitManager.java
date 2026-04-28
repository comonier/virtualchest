package com.comonier.virtualchest;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.configuration.file.FileConfiguration;

public class ChestLimitManager {
    private final FileConfiguration config;

    public ChestLimitManager(FileConfiguration config) {
        this.config = config;
    }

    public String getLimitDisplay(OfflinePlayer target) {
        // Prioridade 1: Jogador é OP
        if (target.isOp()) return "op";

        if (target.isOnline() && target.getPlayer() != null) {
            // Prioridade 2: Possui permissão de admin ou coringa (*)
            if (target.getPlayer().hasPermission("virtualchest.admin") || 
                target.getPlayer().hasPermission("virtualchest.*")) {
                return "op";
            }
            
            // Prioridade 3: Buscar o maior valor X em virtualchest.X
            int maxFound = -1;
            for (PermissionAttachmentInfo pai : target.getPlayer().getEffectivePermissions()) {
                String p = pai.getPermission().toLowerCase();
                if (p.startsWith("virtualchest.")) {
                    try {
                        String valStr = p.replace("virtualchest.", "");
                        int val = Integer.parseInt(valStr);
                        if (val > maxFound) maxFound = val;
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            if (maxFound != -1) {
                return String.format("%02d", maxFound);
            }
        }

        // Prioridade 4: Valor padrão do config.yml
        int defaultLimit = config.getInt("max_chests_per_player", 5);
        return String.format("%02d", defaultLimit);
    }

    public int getLimitNumber(OfflinePlayer target) {
        // OPs e Admins não têm limite (infinito)
        if (target.isOp()) return 999;
        
        if (target.isOnline() && target.getPlayer() != null) {
            if (target.getPlayer().hasPermission("virtualchest.admin") || 
                target.getPlayer().hasPermission("virtualchest.*")) {
                return 999;
            }
        }

        // Para os demais, tenta pegar a permissão numérica ou o padrão
        String display = getLimitDisplay(target);
        try {
            return Integer.parseInt(display);
        } catch (NumberFormatException e) {
            return config.getInt("max_chests_per_player", 5);
        }
    }
}
