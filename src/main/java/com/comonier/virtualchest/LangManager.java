package com.comonier.virtualchest;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class LangManager {
    private final Main plugin;

    public LangManager(Main plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        FileConfiguration config = plugin.getConfig();
        String lang = config.getString("language", "en");
        String message = config.getString("messages." + lang + "." + path, "Message not found: " + path);
        // Retorna a mensagem colorida
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
