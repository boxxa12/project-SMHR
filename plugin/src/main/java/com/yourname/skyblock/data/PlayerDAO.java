package com.yourname.skyblock.data;

import com.yourname.skyblock.model.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Reads/writes the `players` table. All methods run synchronous JDBC calls —
 * callers (commands, listeners) should invoke these off the main thread via
 * {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously} to avoid
 * blocking the server, then hop back to the main thread to touch Bukkit API.
 */
public class PlayerDAO {

    private final DatabaseManager db;

    public PlayerDAO(DatabaseManager db) {
        this.db = db;
    }

    public Optional<PlayerData> find(UUID uuid) throws SQLException {        String sql = "SELECT uuid, username, first_join, last_join, island_id, balance FROM players WHERE uuid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rs));
            }
        }
    }

    public java.util.List<PlayerData> findAll() throws SQLException {
        String sql = "SELECT uuid, username, first_join, last_join, island_id, balance FROM players";
        java.util.List<PlayerData> results = new java.util.ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(map(rs));
            }
        }
        return results;
    }

    /** Inserts the player if they don't exist yet, otherwise returns their existing row. */
    public PlayerData findOrCreate(UUID uuid, String username) throws SQLException {
        Optional<PlayerData> existing = find(uuid);
        if (existing.isPresent()) {
            return existing.get();
        }
        PlayerData data = PlayerData.createNew(uuid, username);
        insert(data);
        return data;
    }

    public void insert(PlayerData data) throws SQLException {
        String sql = "INSERT INTO players (uuid, username, first_join, last_join, island_id, balance) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getUsername());
            ps.setLong(3, data.getFirstJoin());
            ps.setLong(4, data.getLastJoin());
            ps.setString(5, data.getIslandId() != null ? data.getIslandId().toString() : null);
            ps.setDouble(6, data.getBalance());
            ps.executeUpdate();
        }
    }

    public void save(PlayerData data) throws SQLException {
        String sql = "UPDATE players SET username = ?, last_join = ?, island_id = ?, balance = ? WHERE uuid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.getUsername());
            ps.setLong(2, data.getLastJoin());
            ps.setString(3, data.getIslandId() != null ? data.getIslandId().toString() : null);
            ps.setDouble(4, data.getBalance());
            ps.setString(5, data.getUuid().toString());
            ps.executeUpdate();
        }
    }

    private PlayerData map(ResultSet rs) throws SQLException {
        String islandIdStr = rs.getString("island_id");
        return new PlayerData(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("username"),
                rs.getLong("first_join"),
                rs.getLong("last_join"),
                islandIdStr != null ? UUID.fromString(islandIdStr) : null,
                rs.getDouble("balance")
        );
    }
}
