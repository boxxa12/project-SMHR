package com.yourname.skyblock.data;

import com.yourname.skyblock.model.IslandData;
import com.yourname.skyblock.model.PlayerData;
import com.yourname.skyblock.model.QuestProgress;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;

/**
 * Copies every row from the plugin's current (source) database into a second
 * (target) database, using the same DAO classes on both sides — this only
 * works because schema.sql is identical for SQLite and MySQL (see that file's
 * header comment for why: no vendor-specific AUTO_INCREMENT, UUID text keys).
 *
 * Typical use: you started on SQLITE, your server grew, and you now want
 * MySQL. Fill in storage.mysql.* in config.yml (type can stay SQLITE for
 * this step — migrate() always targets MySQL), run `/skyblock migrate`,
 * then set storage.type: MYSQL and restart the server.
 */
public class MigrationService {

    private final JavaPlugin plugin;

    public MigrationService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @param source the plugin's currently active database
     * @return a human-readable summary of what was copied
     */
    public String migrateToMysql(DatabaseManager source) throws SQLException {
        if (source.getStorageType() == StorageType.MYSQL) {
            return "Source database is already MYSQL — nothing to migrate.";
        }

        DatabaseManager target = new DatabaseManager(plugin);
        target.connect(StorageType.MYSQL); // reads storage.mysql.* from config.yml, creates schema

        try {
            PlayerDAO sourcePlayers = new PlayerDAO(source);
            IslandDAO sourceIslands = new IslandDAO(source);
            QuestDAO sourceQuests = new QuestDAO(source);

            PlayerDAO targetPlayers = new PlayerDAO(target);
            IslandDAO targetIslands = new IslandDAO(target);
            QuestDAO targetQuests = new QuestDAO(target);

            List<IslandData> islands = sourceIslands.findAll();
            for (IslandData island : islands) {
                targetIslands.insert(island);
            }

            List<IslandDAO.MemberRow> members = sourceIslands.findAllMembers();
            for (IslandDAO.MemberRow member : members) {
                targetIslands.addMember(member.islandId(), member.playerUuid(), member.role());
            }

            List<PlayerData> players = sourcePlayers.findAll();
            for (PlayerData player : players) {
                targetPlayers.insert(player);
            }

            List<QuestDAO.QuestRow> quests = sourceQuests.findAllQuests();
            for (QuestDAO.QuestRow quest : quests) {
                targetQuests.createQuest(quest.id(), quest.name(), quest.description());
            }

            List<QuestProgress> progress = sourceQuests.findAllProgressRows();
            for (QuestProgress p : progress) {
                targetQuests.upsertProgress(p);
            }

            return String.format(
                    "Migration complete: %d players, %d islands, %d island members, %d quests, %d quest progress rows. " +
                            "Now set storage.type: MYSQL in config.yml and restart the server.",
                    players.size(), islands.size(), members.size(), quests.size(), progress.size());
        } finally {
            target.close();
        }
    }
}
