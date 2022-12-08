package me.rachzy.blocktower.data;

import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.models.ArenaModel;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    final static List<ArenaModel> RoomList = new ArrayList<>();

    public static void setup() {
        FileConfiguration arenasConfig = Arenas.get();

        for(String arenaName : arenasConfig.getConfigurationSection("arenas").getKeys(false)) {
            if(arenasConfig.getBoolean(String.format("arenas.%s.isOpen", arenaName))) {
                create(ArenasList.getArenaByName(arenaName));
            }
        }
    }

    public static void create(ArenaModel arena) {
        RoomList.add(arena);
    }

    public static List<ArenaModel> get() {
        return RoomList;
    }
}
