package com.yourname.skyblock.world;

import com.yourname.skyblock.island.GridConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Ensures all island-worlds from GridConfig exist at server startup.
 * On the first boot, creates them as void worlds. On subsequent boots, just loads
 * them if they're not already loaded.
 *
 * TODO: Replace this basic void-world creator with a real schematic loader
 * or KubeJS/datapack-based world generation once you decide on the approach.
 */
public class WorldManager {

    private final JavaPlugin plugin;
    private final GridConfig gridConfig;
    private final Set<String> createdWorlds = new HashSet<>();

    public WorldManager(JavaPlugin plugin, GridConfig gridConfig) {
        this.plugin = plugin;
        this.gridConfig = gridConfig;
    }

    /**
     * Ensures all island-worlds exist (creates them if they don't).
     * Should be called during plugin enable after gridConfig is loaded.
     */
    public void ensureIslandWorlds() {
        for (String worldName : gridConfig.getIslandWorldNames()) {
            ensureWorld(worldName);
        }
    }

    /**
     * Ensures the hub world exists and has correct settings (PvP, build restrictions).
     * For now, just loads it; enforcement of pvp/build rules is a listener TODO.
     */
    public void ensureHubWorld() {
        String hubName = gridConfig.getHubWorldName();
        ensureWorld(hubName);
        // TODO: register a listener to enforce pvp=false, build=false rules in the hub world.
    }

    private void ensureWorld(String worldName) {
        World existing = Bukkit.getWorld(worldName);
        if (existing != null) {
            plugin.getLogger().info("World '" + worldName + "' is already loaded.");
            return;
        }

        // Check if the world folder exists on disk.
        java.io.File worldFolder = new java.io.File(Bukkit.getWorldContainer(), worldName);
        if (worldFolder.exists() && new java.io.File(worldFolder, "level.dat").exists()) {
            // World exists on disk — just load it.
            World world = Bukkit.createWorld(new WorldCreator(worldName));
            plugin.getLogger().info("Loaded existing world '" + worldName + "'.");
            return;
        }

        // World doesn't exist — create it as a void world (no vanilla terrain generation).
        plugin.getLogger().info("Creating new void world '" + worldName + "'...");
        WorldCreator creator = new WorldCreator(worldName);
        creator.type(org.bukkit.WorldType.FLAT);
        creator.generatorSettings(""); // Completely flat/void
        World world = creator.createWorld();
        if (world != null) {
            createdWorlds.add(worldName);
            plugin.getLogger().info("Created void world '" + worldName + "'.");
        } else {
            plugin.getLogger().warning("Failed to create world '" + worldName + "'.");
        }
    }

    /**
     * @return true if this world was created by the plugin during this startup session
     * (as opposed to being loaded from disk or pre-existing).
     */
    public boolean wasJustCreated(String worldName) {
        return createdWorlds.contains(worldName);
    }
}
