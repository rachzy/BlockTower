package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.RoomModel;
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

        String inventoryTitle = e.getView().getTitle();
        String roomsGuiName = new ConfigPuller("config").getString("gui_title");
        System.out.println(inventoryTitle);
        if(inventoryTitle.equals(roomsGuiName)) {
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
