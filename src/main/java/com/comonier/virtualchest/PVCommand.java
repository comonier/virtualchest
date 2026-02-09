package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.Sound; // Importante para o som funcionar
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PVCommand implements CommandExecutor {

    private final Main plugin;
    private final StorageManager storage;

    public PVCommand(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 1. Verifica se é jogador
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores!");
            return true;
        }

        Player player = (Player) sender;
        String prefix = plugin.getMsg("prefix");
        
        // 2. Define o ID do baú (padrão é 1 se não digitar nada)
        String chestId = (args.length > 0) ? args[0] : "1";

        // 3. Verificação de permissão
        if (!player.hasPermission("virtualchest.pv." + chestId)) {
            player.sendMessage(prefix + plugin.getMsg("no_permission"));
            return true;
        }

        // 4. Cria a Interface (GUI)
        String titulo = plugin.getMsg("opened").replace("%id%", chestId);
        Inventory gui = Bukkit.createInventory(player, 54, titulo);

        // 5. Carrega os itens do arquivo .yml
        storage.loadChest(player.getUniqueId().toString(), chestId, gui);

        // 6. Abre o inventário para o jogador
        player.openInventory(gui);

        // 7. Toca o som de baú (Volume 1.0, Tom 1.0)
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);

        // 8. Envia a mensagem de sucesso
        player.sendMessage(prefix + titulo);

        return true;
    }
}
