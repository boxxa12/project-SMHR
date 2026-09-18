package com.yourname.skyblock.model;

import java.util.UUID;

/** In-memory representation of a row in the `players` table. */
public class PlayerData {

    private final UUID uuid;
    private String username;
    private long firstJoin;
    private long lastJoin;
    private UUID islandId; // nullable — null means the player has no island yet
    private double balance;

    public PlayerData(UUID uuid, String username, long firstJoin, long lastJoin, UUID islandId, double balance) {
        this.uuid = uuid;
        this.username = username;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.islandId = islandId;
        this.balance = balance;
    }

    public static PlayerData createNew(UUID uuid, String username) {
        long now = System.currentTimeMillis();
        return new PlayerData(uuid, username, now, now, null, 0);
    }

    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public long getFirstJoin() { return firstJoin; }
    public long getLastJoin() { return lastJoin; }
    public void setLastJoin(long lastJoin) { this.lastJoin = lastJoin; }
    public UUID getIslandId() { return islandId; }
    public void setIslandId(UUID islandId) { this.islandId = islandId; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
