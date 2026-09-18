package com.yourname.skyblock.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Owns the JDBC connection pool (HikariCP) and creates the schema on startup.
 * Everything else in the plugin (DAO classes) gets connections from here via
 * {@link #getConnection()} and never needs to know whether it's talking to
 * SQLite or MySQL.
 *
 * To migrate from SQLite to MySQL later:
 *   1. Set up a MySQL server and database.
 *   2. Change storage.type to MYSQL and fill in storage.mysql.* in config.yml.
 *   3. Run the plugin's migration command (see README) which reads every row
 *      from the old SQLite file and re-inserts it into MySQL using the same
 *      DAO classes — no manual SQL needed, since the schema is identical.
 */
public class DatabaseManager {

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;
    private StorageType storageType;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Opens the connection pool according to config.yml and creates tables if missing. */
    public void connect() throws SQLException {
        FileConfiguration cfg = plugin.getConfig();
        StorageType type = StorageType.valueOf(cfg.getString("storage.type", "SQLITE").toUpperCase());
        connect(type);
    }

    /**
     * Opens the connection pool for an explicit backend, regardless of storage.type in config.yml.
     * Used by the migration command to open a second (target) database while the plugin's main
     * connection stays on the original (source) backend.
     */
    public void connect(StorageType type) throws SQLException {
        FileConfiguration cfg = plugin.getConfig();
        this.storageType = type;

        HikariConfig hikariConfig = new HikariConfig();

        if (storageType == StorageType.SQLITE) {
            File dbFile = new File(plugin.getDataFolder(), cfg.getString("storage.sqlite.file", "database.db"));
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            hikariConfig.setMaximumPoolSize(1); // SQLite only supports one writer at a time
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        } else {
            String host = cfg.getString("storage.mysql.host", "localhost");
            int port = cfg.getInt("storage.mysql.port", 3306);
            String database = cfg.getString("storage.mysql.database", "skyblock");
            String params = cfg.getString("storage.mysql.params", "");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + params;

            hikariConfig.setJdbcUrl(url);
            hikariConfig.setUsername(cfg.getString("storage.mysql.username", "root"));
            hikariConfig.setPassword(cfg.getString("storage.mysql.password", ""));
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hikariConfig.setMaximumPoolSize(cfg.getInt("storage.mysql.pool.maximumPoolSize", 10));
            hikariConfig.setMinimumIdle(cfg.getInt("storage.mysql.pool.minimumIdle", 2));
            hikariConfig.setConnectionTimeout(cfg.getLong("storage.mysql.pool.connectionTimeoutMs", 10000));
        }

        hikariConfig.setPoolName("SkyblockPlugin-Pool-" + storageType);
        this.dataSource = new HikariDataSource(hikariConfig);

        plugin.getLogger().info("Connected to " + storageType + " database.");
    }

    /**
     * Runs schema.sql on a background thread so table creation never blocks the
     * server's main thread / TPS, even if the DB is slow to respond (e.g. a remote
     * MySQL server). The returned future completes on the SAME background thread —
     * hop back to the main thread yourself (Bukkit.getScheduler().runTask(...))
     * before touching any Bukkit API in the completion callback.
     */
    public java.util.concurrent.CompletableFuture<Void> initSchemaAsync(JavaPlugin schedulerPlugin) {
        java.util.concurrent.CompletableFuture<Void> future = new java.util.concurrent.CompletableFuture<>();
        schedulerPlugin.getServer().getScheduler().runTaskAsynchronously(schedulerPlugin, () -> {
            try {
                applySchema();
                future.complete(null);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /** Synchronous schema application — only call this from a thread that's already off the main thread. */
    public void applySchemaBlocking() throws SQLException {
        applySchema();
    }

    /** Executes schema.sql against the active connection to create tables if they don't exist. */
    private void applySchema() throws SQLException {
        String schemaSql = readResource("/schema.sql");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String statement : schemaSql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    private String readResource(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing bundled resource: " + path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                reader.lines().forEach(line -> sb.append(line).append('\n'));
                return sb.toString();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read bundled resource: " + path, e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Database is not connected yet.");
        }
        return dataSource.getConnection();
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
