package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.RoomModel;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        String inventoryTitle = e.getView().getTitle();
        String roomsGuiName = new ConfigPuller("config").getString("gui_title");

        if(playerRoom != null && playerRoom.isGameStarted()) {
            e.setCancelled(true);
        }

        if(inventoryTitle.equals(roomsGuiName) && e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
            String getItemName = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);
            RoomModel getRoom = Rooms.getRoomByName(getItemName);

            if(getRoom != null) {
                try {
                    getRoom.addPlayer(player);
                    player.closeInventory();
                } catch (Exception ex) {
                    player.sendMessage(ex.getMessage());
                }
            }

            e.setCancelled(true);
        }
    }
}
