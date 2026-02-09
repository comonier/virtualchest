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
            
            // Busca o jogador pelo nome (mesmo offline)
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(prefix + "§cJogador nunca entrou no servidor!");
                return true;
            }

            String titulo = "§4Admin: " + target.getName() + " #" + chestId;
            Inventory gui = Bukkit.createInventory(player, 54, titulo);
            
            // Carrega o baú do outro jogador usando o UUID dele
            storage.loadChest(target.getUniqueId().toString(), chestId, gui);
            
            player.openInventory(gui);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 0.5f); // Som mais grave para admin
            player.sendMessage(prefix + "§eInspecionando baú de " + target.getName());
            return true;
        }

        // --- LÓGICA NORMAL (/pv <id>) ---
        String chestId = (args.length > 0) ? args[0] : "1";

        if (!player.hasPermission("virtualchest.pv." + chestId)) {
            player.sendMessage(prefix + plugin.getMsg("no_permission"));
            return true;
        }

        String titulo = plugin.getMsg("opened").replace("%id%", chestId);
        Inventory gui = Bukkit.createInventory(player, 54, titulo);

        storage.loadChest(player.getUniqueId().toString(), chestId, gui);

        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        player.sendMessage(prefix + titulo);

        return true;
    }
}
