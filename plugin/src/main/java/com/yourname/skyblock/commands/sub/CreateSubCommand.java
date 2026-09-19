package com.yourname.skyblock.commands.sub;

import com.yourname.skyblock.SkyblockPlugin;
import com.yourname.skyblock.model.IslandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** `/island create` — reserves a grid slot, writes the DB row, generates the platform, and teleports the player. */
public class CreateSubCommand implements IslandSubCommand {

    private final SkyblockPlugin plugin;

    public CreateSubCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "skyblock.island.create";
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        player.sendMessage(ChatColor.YELLOW + "Creating your island, please wait...");

        plugin.getIslandManager().createIsland(player.getUniqueId())
                .thenAccept(island -> Bukkit.getScheduler().runTask(plugin, () -> onCreated(player, island)))
                .exceptionally(ex -> {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            player.sendMessage(ChatColor.RED + "Could not create your island: " + rootMessage(ex)));
                    return null;
                });
    }

    private void onCreated(Player player, IslandData island) {
        World world = Bukkit.getWorld(island.getWorld());
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Island world '" + island.getWorld() +
                    "' failed to load. Contact an admin.");
            return;
        }

        // Teleport the player to the center of their island.
        Location home = new Location(world, island.getCenterX() + 0.5, island.getCenterY() + 2,
                island.getCenterZ() + 0.5);
        player.teleport(home);

        player.sendMessage(ChatColor.GREEN + "Your island has been created! " +
                "You're standing on your starter platform. Type " + ChatColor.YELLOW + "/island home" +
                ChatColor.GREEN + " to return here later.");
    }

    private String rootMessage(Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }
}
