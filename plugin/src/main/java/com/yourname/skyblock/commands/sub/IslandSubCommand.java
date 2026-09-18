package com.yourname.skyblock.commands.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * One `/island <subcommand>` handler. Implementations should assume the
 * permission has already been checked by {@link com.yourname.skyblock.commands.IslandCommand}
 * before {@link #execute} is called.
 */
public interface IslandSubCommand {

    /** e.g. "create" for `/island create`. */
    String getName();

    /** Permission node required to run this subcommand, or null if anyone can. */
    default String getPermission() {
        return null;
    }

    /** If true, only players (not console) may run this subcommand. */
    default boolean playerOnly() {
        return true;
    }

    /** Short usage line shown in `/island help`, e.g. "/island create". */
    default String getUsage() {
        return "/island " + getName();
    }

    void execute(CommandSender sender, Player playerOrNull, String[] args);

    /** Optional tab-completion for this subcommand's own arguments (args[1..]). */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
