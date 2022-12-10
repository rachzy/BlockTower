package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.models.RoomModel;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        if(playerRoom != null && playerRoom.getRoomStatus() == RoomStatus.ONGAME && !playerRoom.isGameStarted()) {
            e.setCancelled(true);
        }

    }
}
