package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.OfflinePlayer;

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
            
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(prefix + "§cJogador nunca entrou no servidor!");
                return true;
            }

            String titulo = "§4Admin: " + target.getName() + " #" + chestId;
            Inventory gui = Bukkit.createInventory(player, 54, titulo);
            storage.loadChest(target.getUniqueId().toString(), chestId, gui);
            
            player.openInventory(gui);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 0.5f);
            player.sendMessage(prefix + "§eInspecionando baú de " + target.getName());
            return true;
        }

        // --- LÓGICA NORMAL COM LIMITE ---
        String chestIdStr = (args.length > 0) ? args[0] : "1";
        int chestId;

        // Tenta converter o texto para número
        try {
            chestId = Integer.parseInt(chestIdStr);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cUse apenas números para o baú!");
            return true;
        }

        // Verifica o limite do config.yml (Admins ignoram o limite)
        int maxLimit = plugin.getConfig().getInt("max_chests_per_player", 5);
        if (chestId > maxLimit && !player.hasPermission("virtualchest.admin")) {
            String msgLimit = plugin.getMsg("limit_reached").replace("%limit%", String.valueOf(maxLimit));
            player.sendMessage(prefix + msgLimit);
            return true;
        }

        // Verificação de permissão específica (virtualchest.pv.1, etc)
        if (!player.hasPermission("virtualchest.pv." + chestId)) {
            player.sendMessage(prefix + plugin.getMsg("no_permission"));
            return true;
        }

        String titulo = plugin.getMsg("opened").replace("%id%", String.valueOf(chestId));
        Inventory gui = Bukkit.createInventory(player, 54, titulo);

        storage.loadChest(player.getUniqueId().toString(), String.valueOf(chestId), gui);

        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        player.sendMessage(prefix + titulo);

        return true;
    }
}
