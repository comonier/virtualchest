package com.comonier.virtualchest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;

public class Main extends JavaPlugin {
    private StorageManager storage;
    private LangManager langManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_pt.yml", false);
        saveResource("messages_es.yml", false);
        saveResource("messages_ru.yml", false);
        this.langManager = new LangManager(this);

        try {
            this.storage = new StorageManager(this);
        } catch (SQLException e) {
            getLogger().severe(getMsg("sql_fatal_error").replace("%type%", getConfig().getString("storage_type", "SQLITE")));
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new ChestListener(this, storage), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        
        forceRegisterCommand();
        
        getLogger().info(getMsg("plugin_enabled").replace("%type%", getConfig().getString("storage_type", "SQLITE")));
    }

    private void forceRegisterCommand() {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

            Field knownCommandsField = commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            knownCommands.remove("pv");
            knownCommands.remove("vc");
            knownCommands.remove("virtualchest");

        } catch (Exception ignored) {}

        if (getCommand("pv") != null) {
            PVCommand pvExecutor = new PVCommand(this, storage);
            getCommand("pv").setExecutor(pvExecutor);
            getCommand("pv").setTabCompleter(new PVTabCompleter());
        }
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.closeConnection();
        }
        getLogger().info(getMsg("plugin_disabled"));
    }

    public void reloadPlugin() {
        reloadConfig();
        langManager.reloadLang();
    }

    public String getMsg(String path) { 
        return langManager.getMessage(path); 
    }
}
