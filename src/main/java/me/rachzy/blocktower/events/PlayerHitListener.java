package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.RoomModel;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerHitListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        DamageCause playerDamageCause = e.getCause();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        if (playerRoom != null && playerRoom.getRoomStatus() == RoomStatus.ONGAME) {
            if (playerDamageCause == DamageCause.FALL
                    && !new ConfigPuller("config").getBoolean("fall_damage_enabled")) {
                e.setCancelled(true);
                return;
            }

            if (e.getDamage() < player.getHealth()) return;

            player.setHealth(20);
            if (player.getLastDamageCause() instanceof Player) {
                Player killer = ((Player) player.getLastDamageCause()).getPlayer();
                playerRoom.playerKilled(player, killer);
                return;
            }
            playerRoom.playerDeath(player);
        }
    }
}
