package com.yourname.skyblock.commands.sub;

import com.yourname.skyblock.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** `/island delete` — removes the player's island. Confirmation flow is a TODO. */
public class DeleteSubCommand implements IslandSubCommand {

    private final SkyblockPlugin plugin;

    public DeleteSubCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getPermission() {
        return "skyblock.island.delete";
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        // TODO: require a confirmation step (e.g. "/island delete confirm") before
        // actually deleting — a destructive action shouldn't fire on the first command.
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            player.sendMessage(ChatColor.RED + "This will permanently delete your island. " +
                    "Run " + ChatColor.YELLOW + "/island delete confirm" + ChatColor.RED + " to proceed.");
            return;
        }

        plugin.getIslandManager().deleteIsland(player.getUniqueId())
                .thenRun(() -> Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.GREEN + "Your island has been deleted.")))
                .exceptionally(ex -> {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            player.sendMessage(ChatColor.RED + "Could not delete your island: " + rootMessage(ex)));
                    return null;
                });
    }

    private String rootMessage(Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }
}
