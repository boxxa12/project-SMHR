package com.yourname.skyblock.data;

import com.yourname.skyblock.model.IslandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/** Reads/writes the `islands` table. See {@link PlayerDAO} for threading notes. */
public class IslandDAO {

    private final DatabaseManager db;

    public IslandDAO(DatabaseManager db) {
        this.db = db;
    }

    public Optional<IslandData> find(UUID id) throws SQLException {
        String sql = "SELECT id, owner_uuid, world, center_x, center_y, center_z, level, created_at " +
                "FROM islands WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rs));
            }
        }
    }

    public Optional<IslandData> findByOwner(UUID ownerUuid) throws SQLException {
        String sql = "SELECT id, owner_uuid, world, center_x, center_y, center_z, level, created_at " +
                "FROM islands WHERE owner_uuid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerUuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rs));
            }
        }
    }

    public java.util.List<IslandData> findAll() throws SQLException {
        String sql = "SELECT id, owner_uuid, world, center_x, center_y, center_z, level, created_at FROM islands";
        java.util.List<IslandData> results = new java.util.ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(map(rs));
            }
        }
        return results;
    }

    /** A raw (island_id, player_uuid, role) row from `island_members`, used mainly for migration. */
    public record MemberRow(UUID islandId, UUID playerUuid, String role) {}

    public java.util.List<MemberRow> findAllMembers() throws SQLException {
        String sql = "SELECT island_id, player_uuid, role FROM island_members";
        java.util.List<MemberRow> results = new java.util.ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new MemberRow(
                        UUID.fromString(rs.getString("island_id")),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("role")));
            }
        }
        return results;
    }

    public void insert(IslandData island) throws SQLException {
        String sql = "INSERT INTO islands (id, owner_uuid, world, center_x, center_y, center_z, level, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, island.getId().toString());
            ps.setString(2, island.getOwnerUuid().toString());
            ps.setString(3, island.getWorld());
            ps.setInt(4, island.getCenterX());
            ps.setInt(5, island.getCenterY());
            ps.setInt(6, island.getCenterZ());
            ps.setInt(7, island.getLevel());
            ps.setLong(8, island.getCreatedAt());
            ps.executeUpdate();
        }
    }

    public void save(IslandData island) throws SQLException {
        String sql = "UPDATE islands SET level = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, island.getLevel());
            ps.setString(2, island.getId().toString());
            ps.executeUpdate();
        }
    }

    public void addMember(UUID islandId, UUID playerUuid, String role) throws SQLException {
        String sql = "INSERT INTO island_members (island_id, player_uuid, role) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, islandId.toString());
            ps.setString(2, playerUuid.toString());
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }

    private IslandData map(ResultSet rs) throws SQLException {
        return new IslandData(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("owner_uuid")),
                rs.getString("world"),
                rs.getInt("center_x"),
                rs.getInt("center_y"),
                rs.getInt("center_z"),
                rs.getInt("level"),
                rs.getLong("created_at")
        );
    }
}
