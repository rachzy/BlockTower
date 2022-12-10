package me.rachzy.blocktower.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RoomPlayerModel {
    private final Player player;
    private ItemStack[] storedInventoryItems;
    private Location storedLocation;

    public RoomPlayerModel(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack[] getStoredInventoryItems() {
        return storedInventoryItems;
    }

    public Location getStoredLocation() {
        return storedLocation;
    }

    public void setStoredInventoryItems(ItemStack[] items) {
        this.storedInventoryItems = items;
    }

    public void setStoredLocation(Location storedLocation) {
        this.storedLocation = storedLocation;
    }
}
