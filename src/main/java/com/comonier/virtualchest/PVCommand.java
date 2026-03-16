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
            sender.sendMessage(plugin.getMsg("only_players"));
            return true;
        }

        Player player = (Player) sender;
        String prefix = plugin.getMsg("prefix");

        // --- LÓGICA DE RELOAD ---
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("virtualchest.admin")) {
                player.sendMessage(prefix + plugin.getMsg("no_permission"));
                return true;
            }
            plugin.reloadPlugin();
            player.sendMessage(prefix + plugin.getMsg("reload_success"));
            return true;
        }

        // --- LÓGICA DE ADMIN (/pv admin <jogador> <id>) ---
        if (args.length >= 3 && args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("virtualchest.admin")) {
                player.sendMessage(prefix + plugin.getMsg("no_permission"));
                return true;
            }

            String targetName = args[1];
            String chestId = args[2];
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(prefix + plugin.getMsg("player_not_found"));
                return true;
            }

            String adminTitle = plugin.getMsg("admin_inspect")
                    .replace("%player%", target.getName() != null ? target.getName() : targetName)
                    .replace("%id%", chestId);

            openVirtualChest(player, target.getUniqueId().toString(), chestId, adminTitle);
            return true;
        }

        // --- LÓGICA DE LIMITE DINÂMICO ---
        String chestIdStr = (args.length > 0) ? args[0] : "1";
        int requestedId;
        try {
            requestedId = Integer.parseInt(chestIdStr);
            if (requestedId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + plugin.getMsg("invalid_number"));
            return true;
        }

        int playerLimit = plugin.getConfig().getInt("max_chests_per_player", 5);

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String perm = permission.getPermission().toLowerCase();
            if (perm.startsWith("virtualchest.")) {
                try {
                    int value = Integer.parseInt(perm.replace("virtualchest.", ""));
                    if (value > playerLimit) playerLimit = value;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (!player.hasPermission("virtualchest.admin") && requestedId > playerLimit) {
            player.sendMessage(prefix + plugin.getMsg("limit_reached").replace("%limit%", String.valueOf(playerLimit)));
            return true;
        }

        if (!player.hasPermission("virtualchest.use") && !player.hasPermission("virtualchest.admin")) {
            player.sendMessage(prefix + plugin.getMsg("no_permission"));
            return true;
        }

        String title = plugin.getMsg("opened").replace("%id%", String.valueOf(requestedId));
        openVirtualChest(player, player.getUniqueId().toString(), String.valueOf(requestedId), title);

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
