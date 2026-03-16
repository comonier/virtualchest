package com.comonier.virtualchest;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class LangManager {
    private final Main plugin;

    public LangManager(Main plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        FileConfiguration config = plugin.getConfig();
        String lang = config.getString("language", "en");
        
        // Busca a mensagem baseada no idioma definido na config
        String message = config.getString("messages." + lang + "." + path, "Message not found: " + path);
        
        // Traduz códigos de cores (ex: &6 para dourado)
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
