package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PVCommand implements CommandExecutor {
    private final Main plugin;
    private final StorageManager storage;
    private final ChestLimitManager limitManager;
    private final AdminListManager listManager;

    public PVCommand(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.limitManager = new ChestLimitManager(plugin.getConfig());
        this.listManager = new AdminListManager(plugin, storage);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMsg("only_players"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player, label);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("virtualchest.admin")) {
                plugin.reloadPlugin();
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("reload_success"));
            } else {
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("no_permission"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (player.hasPermission("virtualchest.admin")) {
                int page = 1;
                if (args.length >= 2) {
                    try { page = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
                }
                listManager.handleList(player, page, label);
            } else {
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("no_permission"));
            }
            return true;
        }

        String targetUUID = player.getUniqueId().toString();
        String targetName = player.getName();
        OfflinePlayer targetPlayer = player;
        String chestId;

        if (args[0].equalsIgnoreCase("admin") && player.hasPermission("virtualchest.admin")) {
            if (3 > args.length) {
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("usage_admin").replace("%label%", label));
                return true;
            }
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            targetUUID = target.getUniqueId().toString();
            targetName = (target.getName() != null) ? target.getName() : args[1];
            targetPlayer = target;
            chestId = args[2];
        } else {
            chestId = args[0];
        }

        try {
            int id = Integer.parseInt(chestId);
            if (1 > id) {
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("invalid_number"));
                return true;
            }

            int limit = limitManager.getLimitNumber(targetPlayer);
            if (!player.hasPermission("virtualchest.admin") && id > limit) {
                player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("limit_reached").replace("%limit%", String.valueOf(limit)));
                return true;
            }

            String limitDisplay = limitManager.getLimitDisplay(targetPlayer);
            String idFormatted = String.format("%02d", id);
            
            String title = "§0" + targetName + " p:[§2" + idFormatted + "§0][§9" + limitDisplay + "§0]";
            
            ChestHolder holder = new ChestHolder(targetUUID, String.valueOf(id));
            Inventory inv = Bukkit.createInventory(holder, 54, title);
            
            storage.loadChest(targetUUID, String.valueOf(id), inv);
            player.openInventory(inv);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("invalid_number"));
        } catch (Exception e) {
            player.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("internal_error"));
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
            player.sendMessage(plugin.getMsg("help_list").replace("%label%", label));
            player.sendMessage(plugin.getMsg("help_reload").replace("%label%", label));
        }
        player.sendMessage(plugin.getMsg("help_footer"));
    }
}
