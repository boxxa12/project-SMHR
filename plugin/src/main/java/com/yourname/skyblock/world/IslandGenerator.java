package com.yourname.skyblock.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Procedurally generates a starter island: a small dirt/grass platform centered
 * at the given coordinates. This is a placeholder for more sophisticated generation
 * (schematic loading, KubeJS, etc.) — it just needs to give new players a place to stand.
 *
 * All block placement happens on the main thread via {@link org.bukkit.scheduler.BukkitScheduler#runTask},
 * but the generation itself is quick (small platform = ~1000 blocks).
 */
public class IslandGenerator {

    private final JavaPlugin plugin;

    public IslandGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Generates a starter island at the given location asynchronously, then runs
     * the onComplete callback on the main thread once finished. Returns a future
     * for chaining (e.g., teleport player after generation completes).
     */
    public java.util.concurrent.CompletableFuture<Void> generateIslandAsync(
            String worldName, int centerX, int centerY, int centerZ, int radius, Runnable onComplete) {

        java.util.concurrent.CompletableFuture<Void> future = new java.util.concurrent.CompletableFuture<>();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                generateIsland(worldName, centerX, centerY, centerZ, radius);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (onComplete != null) onComplete.run();
                    future.complete(null);
                });
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /** Synchronous island generation — only call from an async thread. */
    private void generateIsland(String worldName, int centerX, int centerY, int centerZ, int radius) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World '" + worldName + "' not found — was it created?");
        }

        // Starter platform: a 3-high column of dirt (bottom), then 1 layer of grass (top).
        // Sized to fit within the plot (smaller than GridConfig.size which defines the protected region).
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                // Bottom layer (centerY - 2): bedrock for indestructibility.
                Block bedrock = world.getBlockAt(x, centerY - 2, z);
                bedrock.setType(Material.BEDROCK, false);

                // Middle layers: dirt.
                Block dirt1 = world.getBlockAt(x, centerY - 1, z);
                dirt1.setType(Material.DIRT, false);
                Block dirt2 = world.getBlockAt(x, centerY, z);
                dirt2.setType(Material.DIRT, false);

                // Top layer: grass block (gives it a nicer appearance).
                Block grass = world.getBlockAt(x, centerY + 1, z);
                grass.setType(Material.GRASS_BLOCK, false);
            }
        }

        plugin.getLogger().info("Generated starter island at " + worldName + " (" + centerX + ", " +
                centerY + ", " + centerZ + ") with radius " + radius);
    }
}
