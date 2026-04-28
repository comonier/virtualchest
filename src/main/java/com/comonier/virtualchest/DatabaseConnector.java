package com.comonier.virtualchest;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    private final Main plugin;
    private Connection connection;
    private final String type;

    public DatabaseConnector(Main plugin) throws SQLException {
        this.plugin = plugin;
        this.type = plugin.getConfig().getString("storage_type", "SQLITE").toUpperCase();
        setupConnection();
        createTable();
    }

    private void setupConnection() throws SQLException {
        if (type.equals("MYSQL")) {
            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String db = plugin.getConfig().getString("mysql.database");
            String user = plugin.getConfig().getString("mysql.username");
            String pass = plugin.getConfig().getString("mysql.password");
            boolean ssl = plugin.getConfig().getBoolean("mysql.useSSL");
            
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=" + ssl, user, pass);
        } else {
            File dbFile = new File(plugin.getDataFolder(), "chests.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }
    }

    private void createTable() throws SQLException {
        try (Statement s = connection.createStatement()) {
            String query = type.equals("MYSQL") ?
                "CREATE TABLE IF NOT EXISTS v_chests (uuid VARCHAR(36), chest_id VARCHAR(10), data LONGTEXT, PRIMARY KEY (uuid, chest_id))" :
                "CREATE TABLE IF NOT EXISTS v_chests (uuid TEXT, chest_id TEXT, data TEXT, PRIMARY KEY (uuid, chest_id))";
            s.execute(query);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getType() {
        return type;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
