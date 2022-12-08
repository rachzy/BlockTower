package me.rachzy.blocktower.data;

import me.rachzy.blocktower.models.ArenaModel;

import java.util.ArrayList;
import java.util.List;

public class ArenasList {
    private static final List<ArenaModel> arenasList = new ArrayList<>();

    public static void addArena(ArenaModel arena) {
        arenasList.add(arena);
    }

    public static List<ArenaModel> getArenas() {
        return arenasList;
    }

    public static ArenaModel getArenaByName(String arenaName) {
        return arenasList.stream().filter(arena -> arena.getName().equals(arenaName)).findFirst().orElse(null);
    }
}
