package me.rachzy.blocktower.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RoomPlayerModel {
    private final Player player;
    private int lives = 5;
    private int kills = 0;
    private Location spawnLocation;
    private ItemStack[] storedInventoryItems;
    private Location storedLocation;

    public RoomPlayerModel(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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

    public void setStoredInventoryItems(ItemStack[] items) {
        this.storedInventoryItems = items;
    }

    public void setStoredLocation(Location storedLocation) {
        this.storedLocation = storedLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
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
