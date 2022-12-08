package me.rachzy.blocktower.models;

import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.functions.ConfigPuller;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaModel {
    private final String name;
    private Integer playerMin;
    private Integer slotAmount;
    private Integer winHeight;
    private Boolean isOpen;
    private List<HashMap<String, Object>> spawns;

    public ArenaModel(String name) {
        this.name = name;
        this.slotAmount = 0;
        this.isOpen = false;
    }

    public String getName() {
        return name;
    }

    public Integer getPlayerMin() {
        return playerMin;
    }

    public Integer getSlotAmount() {
        return slotAmount;
    }

    public Integer getWinHeight() {
        return winHeight;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public List<HashMap<String, Object>> getSpawns() {
        return spawns;
    }

    public HashMap<String, Object> getSpawnById(Integer spawnId) {
        Object[] spawnsToArray = spawns.toArray();
        if(spawnsToArray.length >= spawnId - 1) {
            return (HashMap<String, Object>) spawnsToArray[spawnId];
        }
        return null;
    }

    public void setArenaFile(String key, Object value) {
        FileConfiguration arenasConfig = Arenas.get();
        arenasConfig.addDefault(String.format("arenas.%s.%s", getName(), key), value);
        Arenas.save();
    }

    public void addSlotAmount() {
        this.slotAmount++;
        setArenaFile("slotAmount", this.slotAmount);
    }

    public void createNewSpawn(String spawnId, Location location) {
        System.out.println(location.getBlock().getType().toString());
        setArenaFile(String.format("spawns.%s.x", spawnId), location.getBlockX());
        setArenaFile(String.format("spawns.%s.y", spawnId), location.getBlockY());
        setArenaFile(String.format("spawns.%s.z", spawnId), location.getBlockZ());
        setArenaFile(String.format("spawns.%s.block", spawnId), (location.getBlock().getType().toString()));
    }

    public void open() throws Throwable {
        if(this.slotAmount < 2) {
            throw new Exception(new ConfigPuller("messages").getString("not_enough_slots_to_open"));
        }
        this.isOpen = true;
        setArenaFile("open", true);
    }

    public void close() {
        this.isOpen = false;
        setArenaFile("open", false);
    }

    public void setPlayerMin(Integer playerMin) {
        this.playerMin = playerMin;
    }

    public void setSlotAmount(Integer slotAmount) {
        this.slotAmount = slotAmount;
    }

    public void setWinHeight(Integer winHeight) {
        this.winHeight = winHeight;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public void setSpawns(List<HashMap<String, Object>> spawns) {
        this.spawns = spawns;
    }
}
