package com.yourname.skyblock.island;

import com.yourname.skyblock.data.IslandDAO;
import com.yourname.skyblock.data.PlayerDAO;
import com.yourname.skyblock.model.IslandData;
import com.yourname.skyblock.world.IslandGenerator;
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
    private final IslandGenerator generator;

    public IslandManager(JavaPlugin plugin, IslandDAO islandDAO, PlayerDAO playerDAO, GridConfig gridConfig) {
        this.plugin = plugin;
        this.islandDAO = islandDAO;
        this.playerDAO = playerDAO;
        this.gridConfig = gridConfig;
        this.generator = new IslandGenerator(plugin);
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
     * `islands` row, adds them as OWNER in `island_members`, links their player row to it,
     * and generates a starter platform. Returns a future so callers (commands) can chain
     * teleport/message logic after both DB writes and world generation complete.
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

                // Now generate the starter platform in the world (async, will chain back to main thread).
                // Radius 2 = 5x5 platform (from -2 to +2 blocks from center)
                generator.generateIslandAsync(gridSlot.world(), gridSlot.x(), gridConfig.getHomeY(),
                        gridSlot.z(), 2, null)
                        .thenRun(() -> future.complete(island))
                        .exceptionally(ex -> {
                            future.completeExceptionally(ex);
                            return null;
                        });
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

                UUID islandId = island.get().getId();

                // Delete all island members linked to this island
                islandDAO.deleteMembersByIsland(islandId);

                // Unlink all players from this island
                islandDAO.unlinkPlayersFromIsland(islandId);

                // Delete the island record itself
                islandDAO.deleteById(islandId);

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
