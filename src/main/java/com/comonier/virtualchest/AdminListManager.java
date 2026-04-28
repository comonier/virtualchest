package com.comonier.virtualchest;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.*;

public class AdminListManager {
    private final Main plugin;
    private final StorageManager storage;

    public AdminListManager(Main plugin, StorageManager storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public void handleList(Player admin, int page, String label) {
        Map<String, Set<String>> allData = storage.getPlayersWithChests();
        if (allData.isEmpty()) {
            admin.sendMessage(plugin.getMsg("prefix") + plugin.getMsg("list_empty"));
            return;
        }

        List<String> uuids = new ArrayList<>(allData.keySet());
        int perPage = 8;
        int totalPages = (int) Math.ceil((double) uuids.size() / perPage);

        if (1 > page) page = 1;
        if (page > totalPages) page = totalPages;

        admin.sendMessage(plugin.getMsg("list_header").replace("%page%", String.valueOf(page)).replace("%total%", String.valueOf(totalPages)));

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, uuids.size());

        for (int i = start; end > i; i++) {
            String uuid = uuids.get(i);
            @SuppressWarnings("deprecation")
            OfflinePlayer offP = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            String name = (offP.getName() != null) ? offP.getName() : uuid;

            ComponentBuilder builder = new ComponentBuilder("§e" + name + " ");
            for (String chestId : allData.get(uuid)) {
                String fmtId = String.format("%02d", Integer.parseInt(chestId));
                TextComponent node = new TextComponent("§7[§a" + fmtId + "§7]");
                node.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eOpen " + name + "'s chest #" + fmtId)));
                node.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pv admin " + name + " " + chestId));
                builder.append(node).append(" ");
            }
            admin.spigot().sendMessage(builder.create());
        }

        if (totalPages > 1) {
            ComponentBuilder nav = new ComponentBuilder("\n");
            if (page > 1) {
                TextComponent prev = new TextComponent("§6[◀ Previous] ");
                prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list " + (page - 1)));
                nav.append(prev);
            }
            if (totalPages > page) {
                TextComponent next = new TextComponent(" §6[Next ▶]");
                next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list " + (page + 1)));
                nav.append(next);
            }
            admin.spigot().sendMessage(nav.create());
        }
        admin.sendMessage(plugin.getMsg("list_footer"));
    }
}
