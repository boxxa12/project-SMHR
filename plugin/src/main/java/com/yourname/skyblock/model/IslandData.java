package com.yourname.skyblock.model;

import java.util.UUID;

/** In-memory representation of a row in the `islands` table. */
public class IslandData {

    private final UUID id;
    private final UUID ownerUuid;
    private final String world;
    private int centerX;
    private int centerY;
    private int centerZ;
    private int level;
    private final long createdAt;

    public IslandData(UUID id, UUID ownerUuid, String world, int centerX, int centerY, int centerZ,
                       int level, long createdAt) {
        this.id = id;
        this.ownerUuid = ownerUuid;
        this.world = world;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.level = level;
        this.createdAt = createdAt;
    }

    public static IslandData createNew(UUID ownerUuid, String world, int centerX, int centerY, int centerZ) {
        return new IslandData(UUID.randomUUID(), ownerUuid, world, centerX, centerY, centerZ, 0,
                System.currentTimeMillis());
    }

    public UUID getId() { return id; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getWorld() { return world; }
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    public int getCenterZ() { return centerZ; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public long getCreatedAt() { return createdAt; }
}
