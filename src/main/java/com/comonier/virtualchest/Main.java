package com.comonier.virtualchest;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private StorageManager storage;
    private LangManager langManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_pt.yml", false);

        this.storage = new StorageManager(this);
        this.langManager = new LangManager(this);
        
        getServer().getPluginManager().registerEvents(new ChestListener(this, storage), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        
        if (getCommand("pv") != null) {
            getCommand("pv").setExecutor(new PVCommand(this, storage));
        }
        
        getLogger().info("VirtualChest v1.2 habilitado com sucesso!");
    }

    public void reloadPlugin() {
        reloadConfig();
        if (langManager != null) {
            langManager.reloadLang();
        }
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
