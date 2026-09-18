package com.yourname.skyblock.commands.sub;

import com.yourname.skyblock.SkyblockPlugin;
import com.yourname.skyblock.model.IslandData;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** `/island create` — reserves a grid slot and writes the DB row for a new island. */
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
        player.sendMessage(ChatColor.GREEN + "Your island has been created in world '" + island.getWorld() +
                "' at (" + island.getCenterX() + ", " + island.getCenterY() + ", " + island.getCenterZ() + ")!");
        // TODO: once world generation exists, teleport the player to their new island here.
    }

    private String rootMessage(Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }
}
