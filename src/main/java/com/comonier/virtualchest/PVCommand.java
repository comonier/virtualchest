package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PVCommand implements CommandExecutor {

    private final Main plugin;
    private final StorageManager storage;

    // Construtor atualizado para receber o storage
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
        
        // Se o jogador não digitar o número, assume o baú 1
        String chestId = (args.length > 0) ? args[0] : "1";

        // Verificação de permissão
        if (!player.hasPermission("virtualchest.pv." + chestId)) {
            player.sendMessage(prefix + plugin.getMsg("no_permission"));
            return true;
        }

        // Criar o título e o inventário
        String titulo = plugin.getMsg("opened").replace("%id%", chestId);
        Inventory gui = Bukkit.createInventory(player, 54, titulo);

        // --- NOVIDADE: Carregar itens salvos antes de abrir ---
        storage.loadChest(player.getUniqueId().toString(), chestId, gui);

        player.openInventory(gui);
        player.sendMessage(prefix + titulo);

        return true;
    }
}
