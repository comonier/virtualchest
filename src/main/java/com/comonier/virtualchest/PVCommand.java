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
import org.jetbrains.annotations.NotNull;

public class PVCommand implements CommandExecutor {

    private final Main plugin;
    private final StorageManager storage;

    public PVCommand(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMsg("only_players"));
            return true;
        }

        String prefix = plugin.getMsg("prefix");

        if (args.length == 0) {
            sendHelp(player, label);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("virtualchest.admin")) {
                plugin.reloadPlugin();
                player.sendMessage(prefix + plugin.getMsg("reload_success"));
            } else {
                player.sendMessage(prefix + plugin.getMsg("no_permission"));
            }
            return true;
        }

        String targetUUID = player.getUniqueId().toString();
        String chestId;
        String titleKey = "opened";
        String targetName = player.getName();

        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("virtualchest.admin")) {
                player.sendMessage(prefix + plugin.getMsg("no_permission"));
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(prefix + plugin.getMsg("usage_admin").replace("%label%", label));
                return true;
            }
            
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(prefix + plugin.getMsg("player_not_found"));
                return true;
            }
            
            targetUUID = target.getUniqueId().toString();
            targetName = (target.getName() != null) ? target.getName() : args[1];
            chestId = args[2];
            titleKey = "admin_inspect";
        } else {
            chestId = args[0];
        }

        int id;
        try {
            id = Integer.parseInt(chestId);
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + plugin.getMsg("invalid_number"));
            return true;
        }

        int limit = plugin.getConfig().getInt("max_chests_per_player", 5);
        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            String p = pai.getPermission().toLowerCase();
            if (p.startsWith("virtualchest.")) {
                try {
                    String sub = p.replace("virtualchest.", "");
                    int val = Integer.parseInt(sub);
                    if (val > limit) limit = val;
                } catch (Exception ignored) {}
            }
        }

        if (!player.hasPermission("virtualchest.admin") && id > limit) {
            player.sendMessage(prefix + plugin.getMsg("limit_reached").replace("%limit%", String.valueOf(limit)));
            return true;
        }

        try {
            String title = plugin.getMsg(titleKey).replace("%id%", String.valueOf(id)).replace("%player%", targetName);
            ChestHolder holder = new ChestHolder(targetUUID, String.valueOf(id));
            Inventory inv = Bukkit.createInventory(holder, 54, title);
            
            storage.loadChest(targetUUID, String.valueOf(id), inv);
            player.openInventory(inv);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
        } catch (Exception e) {
            player.sendMessage(prefix + plugin.getMsg("internal_error"));
            plugin.getLogger().severe("Error opening chest " + id + " for " + player.getName());
        }
        
        return true;
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage(plugin.getMsg("help_header"));
        player.sendMessage(plugin.getMsg("prefix") + "§7" + plugin.getMsg("help_info"));
        player.sendMessage(" ");
        player.sendMessage(plugin.getMsg("help_pv").replace("%label%", label));
        if (player.hasPermission("virtualchest.admin")) {
            player.sendMessage(plugin.getMsg("help_admin").replace("%label%", label));
            player.sendMessage(plugin.getMsg("help_reload").replace("%label%", label));
        }
        player.sendMessage(plugin.getMsg("help_footer"));
    }
}
