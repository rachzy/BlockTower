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
        messagesFile.addDefault("set_spawn_invalid_position", "&cYou have to be standing on a wool block to set a new spawn!");
        messagesFile.addDefault("spawn_created", "&aSpawn {spawn_number} successfully created!");
        messagesFile.addDefault("edit_arena_wrong_usage", "&cUse: /blocktower editarena <:arena_name>");
        messagesFile.addDefault("edit_arena_teleport", "&aYou've been teleported to the world of the arena '{arena_name}'!");
        messagesFile.addDefault("save_arena_wrong_usage", "&cUse: /blocktower savearena <:arena_name>");
        messagesFile.addDefault("save_arena_success", "&aYou've successfully saved the arena '{arena_name}'!");
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
        messagesFile.addDefault("resetting_room", "&cThis room is reseting, you can't join yet!");
        messagesFile.addDefault("ongame_room", "&cThis room already started!");
        messagesFile.addDefault("play_player_already_in_room", "&cYou're already in this room!");
        messagesFile.addDefault("play_success", "&aYou've been added to the queue!");
        messagesFile.addDefault("new_player_on_queue", "&a{player_name} &ehas been added to the queue. &a({current_players}/{total_slots})");
        messagesFile.addDefault("player_left_queue", "&a{player_name} &chas left the queue. &a({current_players}/{total_slots})");
        messagesFile.addDefault("quit_no_rooms", "&cYou're not in a game queue!");
        messagesFile.addDefault("quit_success", "&cYou left your current game.");
        messagesFile.addDefault("game_starting_countdown", "&bThe game will start in &e{time_in_seconds} seconds.");
        messagesFile.addDefault("player_left_game", "&6{player_name} &cleft the game. &b({current_players}/{total_slots})");
        messagesFile.addDefault("game_freezetime_subtitle", "&bStarting in &e{time_in_seconds}...");
        messagesFile.addDefault("game_started_title", "&a&lFight!");
        messagesFile.addDefault("scoreboard_current_height", "&bYour height: &e{current_height}");
        messagesFile.addDefault("scoreboard_win_height", "&bWin height: &a{win_height}");
        messagesFile.addDefault("scoreboard_kills", "&bKills: &6{player_kills}");
        messagesFile.addDefault("scoreboard_remaining_lives", "&bRemaining lives: &c{player_lives}");
        messagesFile.addDefault("player_killed", "&b{player_name} &cwas killed by &6{killer_name}");
        messagesFile.addDefault("player_died", "&b{player_name} &cdied.");
        messagesFile.addDefault("player_kill", "&a+1 kill!");
        messagesFile.addDefault("player_lost", "&cYou lost. Use '/bt quit' to leave the match.");
        messagesFile.addDefault("player_eliminated", "&6{player_name} &cwas eliminated.");
        messagesFile.addDefault("victory_title", "&a&lVICTORY!");
        messagesFile.addDefault("victory_subtitle", "&eYou won! Congratulations!");
        messagesFile.addDefault("defeat_title", "&c&lDEFEAT!");
        messagesFile.addDefault("defeat_subtitle", "&eYou lost!");
        messagesFile.addDefault("victory_message", "&b{player_name} &6won! &e&lCONGRATULATIONS!");
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
