package com.comonier.virtualchest;

import org.bukkit.plugin.java.JavaPlugin;

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
        
        // 3. Registro de Eventos
        getServer().getPluginManager().registerEvents(new ChestListener(this, storage), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        
        // 4. Registro de Comando (Compatível com paper-plugin.yml)
        if (getCommand("pv") != null) {
            getCommand("pv").setExecutor(new PVCommand(this, storage));
        }

        getLogger().info("VirtualChest habilitado com suporte a Folia!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VirtualChest desabilitado.");
    }
    
    public String getMsg(String path) {
        return langManager.getMessage(path);
    }

    public StorageManager getStorage() {
        return storage;
    }
}
