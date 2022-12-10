package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Rooms.get().forEach(room -> {
            if(room.getPlayerByUuid(player.getUniqueId()) != null) {
                room.removePlayer(player);
            }
        });
    }
}
