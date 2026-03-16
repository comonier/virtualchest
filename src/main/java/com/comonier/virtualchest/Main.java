package com.comonier.virtualchest;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private StorageManager storage;
    private LangManager langManager;

    @Override
    public void onEnable() {
        // 1. Configuração inicial e salvamento dos arquivos de tradução
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_pt.yml", false);

        // 2. Inicialização dos Gerenciadores
        this.storage = new StorageManager(this);
        this.langManager = new LangManager(this);
        
        // 3. Registro de Eventos (Listeners)
        getServer().getPluginManager().registerEvents(new ChestListener(this, storage), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        
        // 4. Registro de Comando (Modo compatibilidade para Paper/Folia)
        if (getCommand("pv") != null) {
            getCommand("pv").setExecutor(new PVCommand(this, storage));
        }
        
        getLogger().info("VirtualChest v1.1 habilitado (Suporte a Folia & Multi-Language)!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VirtualChest desabilitado.");
    }
    
    // Método centralizado para buscar mensagens traduzidas
    public String getMsg(String path) {
        return langManager.getMessage(path);
    }

    public StorageManager getStorage() {
        return storage;
    }
}
