package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.models.RoomModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block brokeBlock = e.getBlock();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);
        if(playerRoom != null && playerRoom.isGameStarted()) {
            e.setCancelled(true);

            // Check if the block is not a spawn block
            List<Location> spawnLocations = new ArrayList<>();
            playerRoom.getPlayerList().forEach(playerInList -> {
                spawnLocations.add(playerRoom.getRoomPlayer(playerInList).getSpawnLocation());
            });

            if(spawnLocations.contains(brokeBlock.getLocation())) return;
            brokeBlock.setType(Material.AIR);
        }
    }
}
