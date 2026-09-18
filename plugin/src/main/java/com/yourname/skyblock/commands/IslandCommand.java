package com.yourname.skyblock.commands;

import com.yourname.skyblock.commands.sub.CreateSubCommand;
import com.yourname.skyblock.commands.sub.DeleteSubCommand;
import com.yourname.skyblock.commands.sub.HomeSubCommand;
import com.yourname.skyblock.commands.sub.IslandSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Routes `/island <sub> [args...]` (and its `/is` alias) to the matching
 * {@link IslandSubCommand}. This class only handles routing, permission
 * checks, and player-only checks — actual gameplay logic lives in each
 * subcommand class (and, from there, in {@link com.yourname.skyblock.island.IslandManager}).
 */
public class IslandCommand implements CommandExecutor, TabCompleter {

    private final Map<String, IslandSubCommand> subCommands = new LinkedHashMap<>();

    public IslandCommand(com.yourname.skyblock.SkyblockPlugin plugin) {
        register(new CreateSubCommand(plugin));
        register(new DeleteSubCommand(plugin));
        register(new HomeSubCommand(plugin));
    }

    private void register(IslandSubCommand sub) {
        subCommands.put(sub.getName().toLowerCase(), sub);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        IslandSubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand '" + args[0] + "'.");
            sendHelp(sender);
            return true;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }

        if (sub.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use " + sub.getUsage() + ".");
            return true;
        }

        Player player = sender instanceof Player ? (Player) sender : null;
        sub.execute(sender, player, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return subCommands.values().stream()
                    .filter(sub -> sub.getPermission() == null || sender.hasPermission(sub.getPermission()))
                    .map(IslandSubCommand::getName)
                    .filter(name -> name.startsWith(partial))
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            IslandSubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                return sub.tabComplete(sender, args);
            }
        }

        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- Skyblock Island Commands ---");
        for (IslandSubCommand sub : subCommands.values()) {
            if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(ChatColor.YELLOW + sub.getUsage());
            }
        }
    }
}
