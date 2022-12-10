package me.rachzy.blocktower.data;

import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.models.ArenaModel;
import me.rachzy.blocktower.models.RoomModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    final static List<RoomModel> RoomList = new ArrayList<>();

    public static void setup() {
        FileConfiguration arenasConfig = Arenas.get();

        for(String arenaName : arenasConfig.getConfigurationSection("arenas").getKeys(false)) {
            if(arenasConfig.getBoolean(String.format("arenas.%s.isOpen", arenaName))) {
                create(ArenasList.getArenaByName(arenaName));
            }
        }
    }

    public static void create(ArenaModel arena) {
        RoomList.add(new RoomModel(arena));
    }

    public static RoomModel getRoomByName(String roomName) {
        return RoomList.stream().filter(room -> room.getName().equals(roomName)).findFirst().orElse(null);
    }

    public static List<RoomModel> get() {
        return RoomList;
    }

    public static RoomModel getRoomByPlayer(Player player) {
        return RoomList.stream()
                .filter(room -> room.getPlayerByUuid(player.getUniqueId()) != null)
                .findFirst()
                .orElse(null);
    }
}
