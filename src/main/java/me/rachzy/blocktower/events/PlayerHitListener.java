package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.RoomModel;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.time.LocalDateTime;
import java.util.HashMap;

public class PlayerHitListener implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        // If entity is not a player
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        DamageCause playerDamageCause = e.getCause();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        // If the player is in a room and the room is on game
        if (playerRoom != null && playerRoom.getRoomStatus() == RoomStatus.ONGAME) {
            // Cancel fall damage if it's not enabled
            if (playerDamageCause == DamageCause.FALL
                    && !new ConfigPuller("config").getBoolean("fall_damage_enabled")) {
                e.setCancelled(true);
                return;
            }

            // Checks if the damage would kill the player
            if (e.getDamage() < player.getHealth()) return;

            // If the damage cause was an entity, returns, since another event will be responsible for it
            if (player.getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) return;

            // Heals the player's life
            player.setHealth(20);

            // Causes player's death
            playerRoom.playerDeath(player);
        }
    }

    @EventHandler
    public void onEntityDamagedByOtherEntity(EntityDamageByEntityEvent e) {
        // If entity is not a player
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);

        // If the player is in a room and the room is on game
        if (playerRoom != null && playerRoom.getRoomStatus() == RoomStatus.ONGAME) {
            // Checks if the damage would kill the player
            if (e.getDamage() < player.getHealth()) {
                HashMap<String, Object> damage = new HashMap<>();
                damage.put("damager", damager);
                damage.put("time", LocalDateTime.now());
                playerRoom.getRoomPlayer(player).setLastDamageReceived(damage);
                return;
            }

            // Heals the player's life
            player.setHealth(20);

            // Causes player kill
            playerRoom.playerKilled(player, damager);
        }
    }
}
