package me.rachzy.blocktower;

import me.rachzy.blocktower.commands.BlocktowerCommand;
import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.files.Messages;
import org.bukkit.Utility;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;

public final class BlockTower extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("BlockTower plugin successfully enabled!");

        // Load config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Load messages.yml
        Messages.setup();
        Messages.get().options().copyDefaults(true);
        Messages.save();

        // Load arenas.yml
        Arenas.setup();
        Arenas.get().options().copyDefaults(true);
        Arenas.save();

        // Setup Rooms
        Rooms.setup();

        // Register commands
        getCommand("blocktower").setExecutor(new BlocktowerCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("BlockTower plugin successfully disabled...");
    }
}
