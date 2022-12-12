package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.models.RoomModel;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        if (playerRoom != null && playerRoom.getRoomStatus() == RoomStatus.ONGAME) {
            if (!playerRoom.isGameStarted()) {
                e.setCancelled(true);
                return;
            }
            if (player.getLocation().getY() < -10) {
                HashMap<String, Object> lastDamage = playerRoom.getRoomPlayer(player).getLastDamageReceived();

                if(lastDamage == null) {
                    playerRoom.playerDeath(player);
                    return;
                }

                Player killer = (Player) lastDamage.get("damager");
                LocalDateTime lastDamageTime = (LocalDateTime) lastDamage.get("time");

                if(killer == null
                        || lastDamageTime == null
                        || lastDamageTime.isAfter(LocalDateTime.now().plus(5, ChronoUnit.SECONDS))
                ) {
                    playerRoom.playerDeath(player);
                    return;
                }
                playerRoom.playerKilled(player, killer);
            }
        }

    }
}
