package com.comonier.virtualchest;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class LangManager {
    private final Main plugin;
    private FileConfiguration langConfig;

    public LangManager(Main plugin) {
        this.plugin = plugin;
        reloadLang();
    }

    public void reloadLang() {
        String lang = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) {
            langFile = new File(plugin.getDataFolder(), "messages_en.yml");
        }
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String message = langConfig.getString(path, "&c[Mensagem ausente: " + path + "]");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
