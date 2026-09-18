package com.yourname.skyblock.data;

import com.yourname.skyblock.model.QuestProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Reads/writes the `quests` and `player_quests` tables. See {@link PlayerDAO} for threading notes. */
public class QuestDAO {

    private final DatabaseManager db;

    public QuestDAO(DatabaseManager db) {
        this.db = db;
    }

    /** A raw (id, name, description) row from `quests`, used mainly for migration. */
    public record QuestRow(UUID id, String name, String description) {}

    public java.util.List<QuestRow> findAllQuests() throws SQLException {
        String sql = "SELECT id, name, description FROM quests";
        java.util.List<QuestRow> results = new java.util.ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new QuestRow(UUID.fromString(rs.getString("id")), rs.getString("name"),
                        rs.getString("description")));
            }
        }
        return results;
    }

    public java.util.List<QuestProgress> findAllProgressRows() throws SQLException {
        String sql = "SELECT player_uuid, quest_id, status, progress, completed_at FROM player_quests";
        java.util.List<QuestProgress> results = new java.util.ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(map(rs));
            }
        }
        return results;
    }

    public void createQuest(UUID id, String name, String description) throws SQLException {
        String sql = "INSERT INTO quests (id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, name);
            ps.setString(3, description);
            ps.executeUpdate();
        }
    }

    public Optional<QuestProgress> findProgress(UUID playerUuid, UUID questId) throws SQLException {
        String sql = "SELECT player_uuid, quest_id, status, progress, completed_at FROM player_quests " +
                "WHERE player_uuid = ? AND quest_id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, questId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(map(rs));
            }
        }
    }

    public List<QuestProgress> findAllProgress(UUID playerUuid) throws SQLException {
        String sql = "SELECT player_uuid, quest_id, status, progress, completed_at FROM player_quests " +
                "WHERE player_uuid = ?";
        List<QuestProgress> results = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(map(rs));
                }
            }
        }
        return results;
    }

    public void upsertProgress(QuestProgress progress) throws SQLException {
        Optional<QuestProgress> existing = findProgress(progress.getPlayerUuid(), progress.getQuestId());
        if (existing.isPresent()) {
            String sql = "UPDATE player_quests SET status = ?, progress = ?, completed_at = ? " +
                    "WHERE player_uuid = ? AND quest_id = ?";
            try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, progress.getStatus().name());
                ps.setInt(2, progress.getProgress());
                if (progress.getCompletedAt() != null) {
                    ps.setLong(3, progress.getCompletedAt());
                } else {
                    ps.setNull(3, java.sql.Types.BIGINT);
                }
                ps.setString(4, progress.getPlayerUuid().toString());
                ps.setString(5, progress.getQuestId().toString());
                ps.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO player_quests (player_uuid, quest_id, status, progress, completed_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, progress.getPlayerUuid().toString());
                ps.setString(2, progress.getQuestId().toString());
                ps.setString(3, progress.getStatus().name());
                ps.setInt(4, progress.getProgress());
                if (progress.getCompletedAt() != null) {
                    ps.setLong(5, progress.getCompletedAt());
                } else {
                    ps.setNull(5, java.sql.Types.BIGINT);
                }
                ps.executeUpdate();
            }
        }
    }

    private QuestProgress map(ResultSet rs) throws SQLException {
        long completedAt = rs.getLong("completed_at");
        Long completedAtBoxed = rs.wasNull() ? null : completedAt;
        return new QuestProgress(
                UUID.fromString(rs.getString("player_uuid")),
                UUID.fromString(rs.getString("quest_id")),
                QuestProgress.Status.valueOf(rs.getString("status")),
                rs.getInt("progress"),
                completedAtBoxed
        );
    }
}
