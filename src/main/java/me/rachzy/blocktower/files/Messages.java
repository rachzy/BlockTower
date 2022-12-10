package me.rachzy.blocktower.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messages {
    private static File file;
    private static FileConfiguration messagesFile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("BlockTower").getDataFolder(), "messages.yml");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new Error("Couldn't create messages.yml file... " + e.getMessage());
            }
        }

        messagesFile = YamlConfiguration.loadConfiguration(file);

        messagesFile.addDefault("not_a_player", "&cThis command can only be executed by players!");
        messagesFile.addDefault("no_permission", "&cYou don't have permission to use this command!");
        messagesFile.addDefault("invalid_command", "&cYou entered an invalid command. Use &e/blocktower &cto get a list of available commands");
        messagesFile.addDefault("invalid_arena_name", "&cCouldn't find an arena with that name!");
        messagesFile.addDefault("create_arena_wrong_usage", "&cUse: /blocktower createarena <:arena_name>");
        messagesFile.addDefault("arena_created", "&aThe world of your arena has been successfully created!");
        messagesFile.addDefault("arena_name_already_in_use", "&cThis room name is already in use!");
        messagesFile.addDefault("set_spawn_wrong_usage", "&cUse: /blocktower setspawn <:arena_name> <:spawn_number>");
        messagesFile.addDefault("set_spawn_invalid_args", "&cThe number of the spawn has to be an integer!");
        messagesFile.addDefault("set_spawn_invalid_position", "&cYou have to be standing on a block to set a new spawn!");
        messagesFile.addDefault("spawn_created", "&aSpawn {spawn_number} successfully created!");
        messagesFile.addDefault("edit_arena_wrong_usage", "&cUse: /blocktower editarena <:arena_name>");
        messagesFile.addDefault("edit_arena_teleport", "&aYou've been teleported to the world of the arena '{arena_name}'!");
        messagesFile.addDefault("set_win_height_wrong_usage", "&cUse: /blocktower setheight <:arena_name> <:height>");
        messagesFile.addDefault("set_win_height_wrong_args", "&cThe height value must be an integer!");
        messagesFile.addDefault("set_win_height_invalid_height", "&cThe height value must range from 0 to 256!");
        messagesFile.addDefault("set_win_height_success", "&aYou've set the win height of {arena_name} to {height}");
        messagesFile.addDefault("create_room_wrong_usage", "&cUse: /blocktower createroom <:arena_name>");
        messagesFile.addDefault("not_enough_slots_to_open", "&cThe arena needs to have at least 2 slots to be opened! (Use: '/blocktower setspawn' to create player spawns)");
        messagesFile.addDefault("null_win_height", "&cYou need to set a winheight for the arena! (Use: '/blocktower setwinheight {arena_name} <:height>')");
        messagesFile.addDefault("create_room_already_created", "&cThis room is already created and open!");
        messagesFile.addDefault("create_room_success", "&aThe room {arena_name} was successfully created and now is open to play!");
        messagesFile.addDefault("force_start_room_not_found", "&cYou have to be in a room to use that!");
        messagesFile.addDefault("delete_room_wrong_usage", "&cUse: /blocktower deleteroom <:arena_name>");
        messagesFile.addDefault("delete_room_not_found", "&cCouldn't find an open room for that arena.");
        messagesFile.addDefault("delete_room_success", "&aThe room {arena_name} was successfully deleted!");
        messagesFile.addDefault("play_wrong_usage", "&cUse: /blocktower play <:arena_name>");
        messagesFile.addDefault("full_room", "&cThis room is full!");
        messagesFile.addDefault("play_player_already_in_room", "&cYou're already in this room!");
        messagesFile.addDefault("play_success", "&aYou've been added to the queue!");
        messagesFile.addDefault("new_player_on_queue", "&a{player_name} &ehas been added to the queue. &a({current_players}/{total_slots})");
        messagesFile.addDefault("player_left_queue", "&a{player_name} &chas left the queue. &a({current_players}/{total_slots})");
        messagesFile.addDefault("leavequeue_no_rooms", "&cYou're not in a game queue!");
        messagesFile.addDefault("leavequeue_success", "&cYou left your current game queue.");
        messagesFile.addDefault("game_starting", "&bThe game will start in &e{time_in_seconds} seconds.");
    }

    public static FileConfiguration get() {
        return messagesFile;
    }

    public static void save() {
        try {
            messagesFile.save(file);
        } catch (IOException e) {
            System.out.println("Couldn't save messages.yml file... " + e.getMessage());
        }
    }

    public static void reload() {
        messagesFile = YamlConfiguration.loadConfiguration(file);
    }
}
