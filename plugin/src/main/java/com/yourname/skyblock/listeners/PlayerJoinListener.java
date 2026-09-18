package com.yourname.skyblock.listeners;

import com.yourname.skyblock.data.PlayerDAO;
import com.yourname.skyblock.model.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

/**
 * Ensures every player who joins has a row in the `players` table
 * (creating one on their very first join) and bumps their last-seen time.
 */
public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;
    private final PlayerDAO playerDAO;

    public PlayerJoinListener(JavaPlugin plugin, PlayerDAO playerDAO) {
        this.plugin = plugin;
        this.playerDAO = playerDAO;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        // Never touch JDBC on the main thread — hop async, then log any error back on main.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PlayerData data = playerDAO.findOrCreate(player.getUniqueId(), player.getName());
                data.setUsername(player.getName());
                data.setLastJoin(System.currentTimeMillis());
                playerDAO.save(data);
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to load/save player data for " + player.getName() + ": " + e.getMessage());
            }
        });
    }
}
