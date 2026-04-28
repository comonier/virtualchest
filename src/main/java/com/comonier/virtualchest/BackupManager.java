package com.comonier.virtualchest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class BackupManager {
    private final Main plugin;
    private final DatabaseConnector connector;

    public BackupManager(Main plugin, DatabaseConnector connector) {
        this.plugin = plugin;
        this.connector = connector;
    }

    public void runBackup() {
        try {
            File backupDir = new File(plugin.getDataFolder(), "backups");
            if (!backupDir.exists()) backupDir.mkdirs();

            deleteOldBackups(backupDir);

            String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date());
            File backupFile = new File(backupDir, "backup-" + timeStamp + ".db");

            if (connector.getType().equals("SQLITE")) {
                File dbFile = new File(plugin.getDataFolder(), "chests.db");
                if (dbFile.exists()) {
                    Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info(plugin.getMsg("backup_success").replace("%file%", backupFile.getName()));
                }
            } else {
                backupMySQLToLocal(backupFile);
            }
        } catch (Exception e) {
            plugin.getLogger().severe(plugin.getMsg("backup_error"));
        }
    }

    private void deleteOldBackups(File backupDir) {
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".db"));
        int maxBackups = plugin.getConfig().getInt("max_backups_to_keep", 30);
        
        if (files == null || files.length < maxBackups) return;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        int filesToDelete = files.length - (maxBackups - 1);
        for (int i = 0; i < filesToDelete; i++) {
            files[i].delete();
        }
    }

    private void backupMySQLToLocal(File backupFile) {
        try (Connection backupConn = DriverManager.getConnection("jdbc:sqlite:" + backupFile.getAbsolutePath());
             Statement s = backupConn.createStatement()) {
            
            s.execute("CREATE TABLE IF NOT EXISTS v_chests (uuid TEXT, chest_id TEXT, data TEXT, PRIMARY KEY (uuid, chest_id))");
            
            try (PreparedStatement select = connector.getConnection().prepareStatement("SELECT * FROM v_chests");
                 ResultSet rs = select.executeQuery()) {
                
                while (rs.next()) {
                    try (PreparedStatement insert = backupConn.prepareStatement(
                            "INSERT OR REPLACE INTO v_chests (uuid, chest_id, data) VALUES (?, ?, ?)")) {
                        insert.setString(1, rs.getString("uuid"));
                        insert.setString(2, rs.getString("chest_id"));
                        insert.setString(3, rs.getString("data"));
                        insert.executeUpdate();
                    }
                }
            }
            plugin.getLogger().info(plugin.getMsg("backup_success").replace("%file%", backupFile.getName()));
        } catch (Exception e) {
            plugin.getLogger().severe(plugin.getMsg("backup_error"));
        }
    }
}
