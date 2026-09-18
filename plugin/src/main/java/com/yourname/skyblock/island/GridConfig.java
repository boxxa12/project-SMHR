package com.yourname.skyblock.island;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Typed view over the `worlds` / `island` sections of config.yml.
 * Nothing here touches the database or Bukkit worlds directly — it's pure
 * config data, read once on enable and handed to {@link IslandManager}.
 */
public class GridConfig {

    private final String hubWorldName;
    private final boolean hubPvp;
    private final boolean hubAllowBuild;

    private final List<String> islandWorldNames;

    private final int spacing;
    private final int size;
    private final int homeY;
    private final int originX;
    private final int originZ;
    private final int maxIslandsPerWorld;

    private GridConfig(String hubWorldName, boolean hubPvp, boolean hubAllowBuild,
                        List<String> islandWorldNames, int spacing, int size, int homeY,
                        int originX, int originZ, int maxIslandsPerWorld) {
        this.hubWorldName = hubWorldName;
        this.hubPvp = hubPvp;
        this.hubAllowBuild = hubAllowBuild;
        this.islandWorldNames = islandWorldNames;
        this.spacing = spacing;
        this.size = size;
        this.homeY = homeY;
        this.originX = originX;
        this.originZ = originZ;
        this.maxIslandsPerWorld = maxIslandsPerWorld;
    }

    public static GridConfig load(JavaPlugin plugin) {
        var cfg = plugin.getConfig();

        String hubWorldName = cfg.getString("worlds.hub.name", "world");
        boolean hubPvp = cfg.getBoolean("worlds.hub.pvp", false);
        boolean hubAllowBuild = cfg.getBoolean("worlds.hub.allow-build", false);

        List<String> islandWorldNames = new ArrayList<>();
        List<?> rawList = cfg.getList("worlds.islands");
        if (rawList != null) {
            for (Object entry : rawList) {
                if (entry instanceof ConfigurationSection section) {
                    islandWorldNames.add(section.getString("name"));
                } else if (entry instanceof java.util.Map<?, ?> map && map.get("name") != null) {
                    islandWorldNames.add(String.valueOf(map.get("name")));
                }
            }
        }
        if (islandWorldNames.isEmpty()) {
            islandWorldNames.add("islands_1");
        }

        int spacing = cfg.getInt("island.spacing", 256);
        int size = cfg.getInt("island.size", 64);
        int homeY = cfg.getInt("island.home-y", 100);
        int originX = cfg.getInt("island.origin-x", 0);
        int originZ = cfg.getInt("island.origin-z", 0);
        int maxIslandsPerWorld = cfg.getInt("island.max-islands-per-world", 500);

        return new GridConfig(hubWorldName, hubPvp, hubAllowBuild, islandWorldNames,
                spacing, size, homeY, originX, originZ, maxIslandsPerWorld);
    }

    public String getHubWorldName() { return hubWorldName; }
    public boolean isHubPvp() { return hubPvp; }
    public boolean isHubAllowBuild() { return hubAllowBuild; }
    public List<String> getIslandWorldNames() { return islandWorldNames; }
    public int getSpacing() { return spacing; }
    public int getSize() { return size; }
    public int getHomeY() { return homeY; }
    public int getOriginX() { return originX; }
    public int getOriginZ() { return originZ; }
    public int getMaxIslandsPerWorld() { return maxIslandsPerWorld; }
}
