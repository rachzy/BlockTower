package me.rachzy.blocktower.models;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.functions.ConfigPuller;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.tools.JavaCompiler;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaModel {
    private final String name;
    private Integer playerMin;
    private Integer slotAmount;
    private Integer winHeight;
    private Boolean isOpen;
    private List<HashMap<String, Object>> spawns;
    private HashMap<String, Location> editors = new HashMap<>();

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
            return spawns.get(spawnId);
        }
        return null;
    }

    public void setArenaFile(String key, Object value) {
        FileConfiguration arenasConfig = Arenas.get();
        arenasConfig.set(String.format("arenas.%s.%s", this.getName(), key), value);
        Arenas.save();
    }

    public void addSlotAmount() {
        this.slotAmount++;
        setArenaFile("slotAmount", this.slotAmount);
    }

    public void createNewSpawn(String spawnId, Location location) {
        setArenaFile(String.format("spawns.%s.x", spawnId), location.getBlockX());
        setArenaFile(String.format("spawns.%s.y", spawnId), location.getBlockY());
        setArenaFile(String.format("spawns.%s.z", spawnId), location.getBlockZ());
        setArenaFile(String.format("spawns.%s.block", spawnId), (location.getBlock().getType().toString()));
        this.addSlotAmount();
    }

    public void open() throws Throwable {
        if(this.getSlotAmount() < 2) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("not_enough_slots_to_open"));
        }

        if(this.getWinHeight() == null) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("null_win_height").replace("{arena_name}", this.getName()));
        }

        this.isOpen = true;
        setArenaFile("isOpen", true);
        Rooms.create(this);
    }

    public void close() {
        this.isOpen = false;
        setArenaFile("isOpen", false);
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
        this.isOpen = open;
    }

    public void setSpawns(List<HashMap<String, Object>> spawns) {
        this.spawns = spawns;
    }

    public void addEditor(Player editor) {
        this.editors.put(editor.getDisplayName(), editor.getLocation());
    }

    public Location getEditorStoredLocation(Player editor) {
        return this.editors.get(editor.getDisplayName());
    }

    public void removeEditor(Player editor) {
        this.editors.remove(editor.getUniqueId());
    }
}
