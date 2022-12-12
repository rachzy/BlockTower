package me.rachzy.blocktower.models;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class RoomPlayerModel {
    private final Player player;
    private DyeColor color;
    private int lives = 5;
    private int kills = 0;
    private Location spawnLocation;
    private HashMap<String, Object> lastDamageReceived = new HashMap<>();
    private ItemStack[] storedInventoryItems;
    private Location storedLocation;

    public RoomPlayerModel(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public DyeColor getColor() {
        return color;
    }

    public int getLives() {
        return lives;
    }

    public int getKills() {
        return kills;
    }

    public ItemStack[] getStoredInventoryItems() {
        return storedInventoryItems;
    }

    public Location getStoredLocation() {
        return storedLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public HashMap<String, Object> getLastDamageReceived() {
        return lastDamageReceived;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public void setStoredInventoryItems(ItemStack[] items) {
        this.storedInventoryItems = items;
    }

    public void setStoredLocation(Location storedLocation) {
        this.storedLocation = storedLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setLastDamageReceived(HashMap<String, Object> lastDamageReceived) {
        this.lastDamageReceived = lastDamageReceived;
    }

    public void increaseLives() {
        this.lives++;
    }
    public void decreaseLives() {
        this.lives--;
    }

    public void increaseKills() {
        this.kills++;
    }
}
