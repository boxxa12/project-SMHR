package com.yourname.skyblock.commands.sub;

import com.yourname.skyblock.SkyblockPlugin;
import com.yourname.skyblock.model.IslandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** `/island home` — teleports the player to their island's center. */
public class HomeSubCommand implements IslandSubCommand {

    private final SkyblockPlugin plugin;

    public HomeSubCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getPermission() {
        return "skyblock.island.home";
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var islandOpt = plugin.getIslandManager().getIsland(player.getUniqueId());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (islandOpt.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "You don't have an island yet — try " +
                                ChatColor.YELLOW + "/island create" + ChatColor.RED + ".");
                        return;
                    }
                    teleportHome(player, islandOpt.get());
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "Database error, check server console."));
                plugin.getLogger().warning("DB error in /island home: " + e.getMessage());
            }
        });
    }

    private void teleportHome(Player player, IslandData island) {
        World world = Bukkit.getWorld(island.getWorld());
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Your island's world ('" + island.getWorld() +
                    "') isn't loaded on this server yet.");
            return;
        }
        Location home = new Location(world, island.getCenterX() + 0.5, island.getCenterY(),
                island.getCenterZ() + 0.5);
        player.teleport(home);
        player.sendMessage(ChatColor.GREEN + "Teleported to your island.");
    }
}
