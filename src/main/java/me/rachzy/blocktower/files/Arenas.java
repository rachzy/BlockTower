package me.rachzy.blocktower.files;

import me.rachzy.blocktower.data.ArenasList;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.ArenaModel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arenas {
    public static File file;
    public static FileConfiguration arenasFile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("BlockTower").getDataFolder(), "arenas.yml");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Couldn't create arenas.yml file... " + e.getMessage());
            }
        }

        arenasFile = YamlConfiguration.loadConfiguration(file);

        // Create example arena
        Map<String, Object> exampleArena = new HashMap<>();
        exampleArena.put("arenas.example.name", "example");
        exampleArena.put("arenas.example.playerMin", 0);
        exampleArena.put("arenas.example.slotAmount", 0);
        exampleArena.put("arenas.example.winHeight", 120);
        exampleArena.put("arenas.example.isOpen", false);
        exampleArena.put("arenas.example.spawns.1.x", 120);
        exampleArena.put("arenas.example.spawns.1.y", 60);
        exampleArena.put("arenas.example.spawns.1.z", 120);

        arenasFile.addDefaults(exampleArena);

        // Load arenas
        for(String arenaName : arenasFile.getConfigurationSection("arenas").getKeys(false)) {
            Integer getPlayerMin = arenasFile.getInt(String.format("arenas.%s.playerMin", arenaName));
            Integer getSlotAmount = arenasFile.getInt(String.format("arenas.%s.slotAmount", arenaName));
            Integer getWinHeight = arenasFile.getInt(String.format("arenas.%s.winHeight", arenaName));
            Boolean getArenaOpen = arenasFile.getBoolean(String.format("arenas.%s.isOpen", arenaName));
            ConfigurationSection getArenaSpawnsSection = arenasFile.getConfigurationSection(String.format("arenas.%s.isOpen.spawns", arenaName));
            List<HashMap<String, Object>> getArenaSpawns = new ArrayList<>();

            if(getArenaSpawnsSection != null) {
                for(String spawnValue : getArenaSpawnsSection.getKeys(false)) {
                    HashMap<String, Object> spawnLocations = new HashMap<>();

                    Integer getSpawnX = arenasFile.getInt(String.format("arenas.%s.spawns.%s.x", arenaName, spawnValue));
                    Integer getSpawnY = arenasFile.getInt(String.format("arenas.%s.spawns.%s.y", arenaName, spawnValue));
                    Integer getSpawnZ = arenasFile.getInt(String.format("arenas.%s.spawns.%s.z", arenaName, spawnValue));
                    Material getSpawnBlock = Material.matchMaterial(arenasFile.getString(String.format("arenas.%s.spawns.%s.block", arenaName, spawnValue)));

                    spawnLocations.put("x", getSpawnX);
                    spawnLocations.put("y", getSpawnY);
                    spawnLocations.put("z", getSpawnZ);
                    spawnLocations.put("block", getSpawnBlock);

                    getArenaSpawns.add(spawnLocations);
                }
            }

            ArenaModel newArena = new ArenaModel(arenaName);
            newArena.setPlayerMin(getPlayerMin);
            newArena.setSlotAmount(getSlotAmount);
            newArena.setWinHeight(getWinHeight);
            newArena.setOpen(getArenaOpen);
            newArena.setSpawns(getArenaSpawns);

            ArenasList.addArena(newArena);
        }
    }

    public static FileConfiguration get() {
        return arenasFile;
    }

    public static void createNewArena(String arenaName) throws Exception {
        if(ArenasList.getArenaByName(arenaName) != null) {
            throw new Exception(new ConfigPuller("messages").getString("arena_name_already_in_use"));
        }

        Map<String, Object> newArena = new HashMap<>();
        newArena.put(String.format("arenas.%s.name", arenaName), arenaName);
        newArena.put(String.format("arenas.%s.playerMin", arenaName), 0);
        newArena.put(String.format("arenas.%s.slotAmount", arenaName), 0);
        newArena.put(String.format("arenas.%s.isOpen", arenaName), false);

        arenasFile.addDefaults(newArena);
        ArenasList.addArena(new ArenaModel(arenaName));
        save();
    }

    public static void reload() {
        arenasFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            arenasFile.save(file);
        } catch (Exception e) {
            System.out.println("Couldn't create arenas.yml file... " + e.getMessage());
        }
    }
}
