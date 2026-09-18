package com.yourname.skyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyblockPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SkyblockPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyblockPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("skyblock")) {
            sender.sendMessage("Skyblock plugin is working!");
            return true;
        }
        return false;
    }
}
