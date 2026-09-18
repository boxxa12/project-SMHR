package com.yourname.skyblock.model;

import java.util.UUID;

/** In-memory representation of a row in the `player_quests` table (a player's progress on one quest). */
public class QuestProgress {

    public enum Status {
        IN_PROGRESS,
        COMPLETED
    }

    private final UUID playerUuid;
    private final UUID questId;
    private Status status;
    private int progress;
    private Long completedAt; // nullable

    public QuestProgress(UUID playerUuid, UUID questId, Status status, int progress, Long completedAt) {
        this.playerUuid = playerUuid;
        this.questId = questId;
        this.status = status;
        this.progress = progress;
        this.completedAt = completedAt;
    }

    public static QuestProgress start(UUID playerUuid, UUID questId) {
        return new QuestProgress(playerUuid, questId, Status.IN_PROGRESS, 0, null);
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public UUID getQuestId() { return questId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
}
