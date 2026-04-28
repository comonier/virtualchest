package com.comonier.virtualchest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.*;
import java.sql.*;
import java.util.*;

public class StorageManager {
    private final Main plugin;
    private final DatabaseConnector connector;
    private final BackupManager backupManager;

    public StorageManager(Main plugin) throws SQLException {
        this.plugin = plugin;
        this.connector = new DatabaseConnector(plugin);
        this.backupManager = new BackupManager(plugin, connector);
        
        backupManager.runBackup();
        runMassMigration();
    }

    public void runMassMigration() {
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) return;

        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) return;

        plugin.getLogger().info(plugin.getMsg("migration_started").replace("%count%", String.valueOf(files.length)));

        for (File file : files) {
            String uuid = file.getName().replace(".yml", "");
            FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection chestsSection = yaml.getConfigurationSection("chests");

            if (chestsSection != null) {
                for (String chestId : chestsSection.getKeys(false)) {
                    ConfigurationSection itemSection = chestsSection.getConfigurationSection(chestId);
                    if (itemSection != null) {
                        saveYamlToDb(uuid, chestId, itemSection);
                    }
                }
            }
            file.renameTo(new File(dataDir, file.getName() + ".bak"));
        }
        plugin.getLogger().info(plugin.getMsg("migration_finished"));
    }

    private void saveYamlToDb(String uuid, String chestId, ConfigurationSection section) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOut = new BukkitObjectOutputStream(out);
            
            int size = 54;
            dataOut.writeInt(size);
            for (int i = 0; i < size; i++) {
                dataOut.writeObject(section.get(String.valueOf(i)));
            }
            dataOut.close();
            String encoded = Base64.getEncoder().encodeToString(out.toByteArray());

            String query = connector.getType().equals("MYSQL") ?
                "INSERT INTO v_chests (uuid, chest_id, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)" :
                "INSERT OR REPLACE INTO v_chests (uuid, chest_id, data) VALUES (?, ?, ?)";

            try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
                ps.setString(1, uuid);
                ps.setString(2, chestId);
                ps.setString(3, encoded);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            plugin.getLogger().severe(plugin.getMsg("sql_error")
                .replace("%player%", uuid)
                .replace("%id%", chestId));
        }
    }

    public void saveChest(String uuid, String chestId, Inventory inv) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOut = new BukkitObjectOutputStream(out);
                dataOut.writeInt(inv.getSize());
                for (int i = 0; i < inv.getSize(); i++) {
                    dataOut.writeObject(inv.getItem(i));
                }
                dataOut.close();
                String encoded = Base64.getEncoder().encodeToString(out.toByteArray());

                String query = connector.getType().equals("MYSQL") ?
                    "INSERT INTO v_chests (uuid, chest_id, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)" :
                    "INSERT OR REPLACE INTO v_chests (uuid, chest_id, data) VALUES (?, ?, ?)";

                try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
                    ps.setString(1, uuid);
                    ps.setString(2, chestId);
                    ps.setString(3, encoded);
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                plugin.getLogger().severe(plugin.getMsg("sql_error")
                    .replace("%player%", uuid)
                    .replace("%id%", chestId));
            }
        });
    }

    public void loadChest(String uuid, String chestId, Inventory inv) {
        inv.clear();
        try (PreparedStatement ps = connector.getConnection().prepareStatement(
                "SELECT data FROM v_chests WHERE uuid = ? AND chest_id = ?")) {
            ps.setString(1, uuid);
            ps.setString(2, chestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                byte[] decode = Base64.getDecoder().decode(rs.getString("data"));
                BukkitObjectInputStream dataIn = new BukkitObjectInputStream(new ByteArrayInputStream(decode));
                int size = dataIn.readInt();
                for (int i = 0; i < size; i++) {
                    ItemStack item = (ItemStack) dataIn.readObject();
                    if (item != null) inv.setItem(i, item);
                }
                dataIn.close();
            }
        } catch (Exception e) {
            plugin.getLogger().severe(plugin.getMsg("sql_error")
                .replace("%player%", uuid)
                .replace("%id%", chestId));
        }
    }

    public Map<String, Set<String>> getPlayersWithChests() {
        Map<String, Set<String>> playerMap = new HashMap<>();
        try (PreparedStatement ps = connector.getConnection().prepareStatement("SELECT uuid, chest_id FROM v_chests")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String chestId = rs.getString("chest_id");
                playerMap.computeIfAbsent(uuid, k -> new TreeSet<>()).add(chestId);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching player list from database.");
        }
        return playerMap;
    }

    public void closeConnection() {
        connector.close();
    }
}
