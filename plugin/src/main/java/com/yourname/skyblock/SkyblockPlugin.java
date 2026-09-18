package com.yourname.skyblock;

import com.yourname.skyblock.data.DatabaseManager;
import com.yourname.skyblock.data.IslandDAO;
import com.yourname.skyblock.data.MigrationService;
import com.yourname.skyblock.data.PlayerDAO;
import com.yourname.skyblock.data.QuestDAO;
import com.yourname.skyblock.listeners.PlayerJoinListener;
import com.yourname.skyblock.model.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class SkyblockPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private PlayerDAO playerDAO;
    private IslandDAO islandDAO;
    private QuestDAO questDAO;
    private MigrationService migrationService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to the database — disabling plugin. " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerDAO = new PlayerDAO(databaseManager);
        islandDAO = new IslandDAO(databaseManager);
        questDAO = new QuestDAO(databaseManager);
        migrationService = new MigrationService(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, playerDAO), this);

        getLogger().info("SkyblockPlugin enabled! Storage: " + databaseManager.getStorageType());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("SkyblockPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("skyblock")) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("migrate")) {
            if (!sender.isOp()) {
                sender.sendMessage("Only server operators can run the database migration.");
                return true;
            }
            sender.sendMessage("Starting migration to MySQL, check the console for progress...");
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    String summary = migrationService.migrateToMysql(databaseManager);
                    getLogger().info(summary);
                    sender.sendMessage(summary);
                } catch (SQLException e) {
                    getLogger().severe("Migration failed: " + e.getMessage());
                    sender.sendMessage("Migration failed, check server console for details.");
                }
            });
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        // DB access is done off the main thread so the command never freezes the server.
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                PlayerData data = playerDAO.findOrCreate(player.getUniqueId(), player.getName());
                player.sendMessage("Skyblock plugin is working! Island: " +
                        (data.getIslandId() != null ? data.getIslandId() : "none yet") +
                        " | Balance: " + data.getBalance());
            } catch (SQLException e) {
                player.sendMessage("Database error, check server console.");
                getLogger().warning("DB error in /skyblock command: " + e.getMessage());
            }
        });
        return true;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerDAO getPlayerDAO() {
        return playerDAO;
    }

    public IslandDAO getIslandDAO() {
        return islandDAO;
    }

    public QuestDAO getQuestDAO() {
        return questDAO;
    }
}
