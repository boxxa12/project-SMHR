package com.yourname.skyblock.island;

import com.yourname.skyblock.data.IslandDAO;
import com.yourname.skyblock.data.PlayerDAO;
import com.yourname.skyblock.model.IslandData;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Business-logic layer for islands: sits between the commands and the DAOs.
 * Commands should never touch {@link IslandDAO} directly — go through here so
 * the rules (one island per player, grid slot allocation, etc.) live in one place.
 *
 * The actual world generation (pasting a schematic / building starter platform)
 * is intentionally left as a TODO — this class currently only reserves a grid
 * slot and writes the database row, which is enough to build/test the command
 * flow before generation code exists.
 */
public class IslandManager {

    private final JavaPlugin plugin;
    private final IslandDAO islandDAO;
    private final PlayerDAO playerDAO;
    private final GridConfig gridConfig;

    public IslandManager(JavaPlugin plugin, IslandDAO islandDAO, PlayerDAO playerDAO, GridConfig gridConfig) {
        this.plugin = plugin;
        this.islandDAO = islandDAO;
        this.playerDAO = playerDAO;
        this.gridConfig = gridConfig;
    }

    public GridConfig getGridConfig() {
        return gridConfig;
    }

    /** @return the player's island, if they have one. Runs on the calling thread — call from async context. */
    public Optional<IslandData> getIsland(UUID ownerUuid) throws SQLException {
        return islandDAO.findByOwner(ownerUuid);
    }

    /**
     * Creates a new island for the player: computes their grid slot, writes the
     * `islands` row, adds them as OWNER in `island_members`, and links their
     * player row to it. Does NOT yet generate any blocks in the world — that's
     * the next piece of work (schematic paste / procedural starter platform).
     *
     * Returns a future so callers (commands) can chain teleport/message logic
     * after the DB write completes, without blocking the main thread.
     */
    public CompletableFuture<IslandData> createIsland(UUID ownerUuid) {
        CompletableFuture<IslandData> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (islandDAO.findByOwner(ownerUuid).isPresent()) {
                    future.completeExceptionally(new IllegalStateException("Player already has an island."));
                    return;
                }

                int slot = countExistingIslands();
                GridSlot gridSlot = resolveSlot(slot);

                IslandData island = IslandData.createNew(ownerUuid, gridSlot.world(),
                        gridSlot.x(), gridConfig.getHomeY(), gridSlot.z());

                islandDAO.insert(island);
                islandDAO.addMember(island.getId(), ownerUuid, "OWNER");
                playerDAO.setIslandId(ownerUuid, island.getId());

                // TODO: generate the starter island (schematic paste or procedural platform)
                // at (gridSlot.world(), gridSlot.x(), gridConfig.getHomeY(), gridSlot.z())
                // before the player is teleported there.

                future.complete(island);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /** Deletes the player's island and all membership/data tied to it. */
    public CompletableFuture<Void> deleteIsland(UUID ownerUuid) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Optional<IslandData> island = islandDAO.findByOwner(ownerUuid);
                if (island.isEmpty()) {
                    future.completeExceptionally(new IllegalStateException("Player has no island to delete."));
                    return;
                }

                // TODO: unlink members' player rows, delete island_members rows,
                // delete the islands row, and clear/regenerate the world region.
                // Left as a stub until the schema-cascade / world-reset strategy is decided.

                future.complete(null);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private int countExistingIslands() throws SQLException {
        return islandDAO.findAll().size();
    }

    /** Deterministically maps a 0-based slot index to a (world, x, z) grid position. */
    private GridSlot resolveSlot(int slotIndex) {
        int perWorld = gridConfig.getMaxIslandsPerWorld();
        int worldIndex = Math.min(slotIndex / perWorld, gridConfig.getIslandWorldNames().size() - 1);
        int localIndex = slotIndex % perWorld;

        // Simple expanding-square spiral could replace this later; a straight row
        // is enough for the initial foundation and is trivial to reason about.
        int x = gridConfig.getOriginX() + localIndex * gridConfig.getSpacing();
        int z = gridConfig.getOriginZ();

        return new GridSlot(gridConfig.getIslandWorldNames().get(worldIndex), x, z);
    }

    private record GridSlot(String world, int x, int z) {}
}
