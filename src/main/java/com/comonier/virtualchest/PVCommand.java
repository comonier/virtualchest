package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PVCommand implements CommandExecutor {

    private final Main plugin;
    private final StorageManager storage;

    public PVCommand(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores!");
            return true;
        }

        Player player = (Player) sender;
        String prefix = plugin.getMsg("prefix");

        // --- LÓGICA DE ADMIN ---
        if (args.length >= 3 && args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("virtualchest.admin")) {
                player.sendMessage(prefix + plugin.getMsg("no_permission"));
                return true;
            }

            String targetName = args[1];
            String chestId = args[2];
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            
            openVirtualChest(player, target.getUniqueId().toString(), chestId, "§4Admin: " + target.getName() + " #" + chestId);
            return true;
        }

        // --- LÓGICA DE LIMITE ---
        String chestIdStr = (args.length > 0) ? args[0] : "1";
        int requestedId;
        try {
            requestedId = Integer.parseInt(chestIdStr);
            if (requestedId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cUse um número válido para o baú!");
            return true;
        }

        // 1. Pega o limite base da config
        int playerLimit = plugin.getConfig().getInt("max_chests_per_player", 5);

        // 2. Procura se o jogador tem uma permissão maior (ex: virtualchest.10)
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String perm = permission.getPermission().toLowerCase();
            if (perm.startsWith("virtualchest.")) {
                try {
                    String suffix = perm.replace("virtualchest.", "");
                    int value = Integer.parseInt(suffix);
                    if (value > playerLimit) {
                        playerLimit = value;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // 3. Verifica se ele é Admin (ignora limite) ou se está dentro do limite
        if (!player.hasPermission("virtualchest.admin") && requestedId > playerLimit) {
            String msgLimit = plugin.getMsg("limit_reached").replace("%limit%", String.valueOf(playerLimit));
            player.sendMessage(prefix + msgLimit);
            return true;
        }

        // Abrir baú
        String titulo = plugin.getMsg("opened").replace("%id%", String.valueOf(requestedId));
        openVirtualChest(player, player.getUniqueId().toString(), String.valueOf(requestedId), titulo);

        return true;
    }

    private void openVirtualChest(Player viewer, String ownerUUID, String chestId, String title) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            Inventory gui = Bukkit.createInventory(viewer, 54, title);
            storage.loadChest(ownerUUID, chestId, gui);

            viewer.getScheduler().run(plugin, t -> {
                viewer.openInventory(gui);
                viewer.playSound(viewer.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
            }, null);
        });
    }
}
