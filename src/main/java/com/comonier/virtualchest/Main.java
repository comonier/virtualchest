package com.comonier.virtualchest;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class Main extends JavaPlugin {

    private StorageManager storage;
    private LangManager langManager;

    @Override
    public void onEnable() {
        // 1. Configuração inicial
        saveDefaultConfig();

        // 2. Inicialização dos Gerenciadores
        this.storage = new StorageManager(this);
        this.langManager = new LangManager(this);
        
        // 3. Registro de Eventos (Listeners)
        getServer().getPluginManager().registerEvents(new ChestListener(this, storage), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        
        // 4. Registro de Comandos
        getCommand("pv").setExecutor(new PVCommand(this, storage));
        
        // Mensagem de sucesso
        Bukkit.getConsoleSender().sendMessage("§a[VirtualChest] Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        // Mensagem de encerramento (importante para o log)
        Bukkit.getConsoleSender().sendMessage("§c[VirtualChest] Plugin Disabled!");
    }
    
    // Método para tradução que agora utiliza a classe LangManager dedicada
    public String getMsg(String path) {
        return langManager.getMessage(path);
    }
}
