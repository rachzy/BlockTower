package me.rachzy.blocktower.events;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.models.RoomModel;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block placedBlock = e.getBlockPlaced();
        RoomModel playerRoom = Rooms.getRoomByPlayer(player);
        if(playerRoom != null
                && playerRoom.isGameStarted()
                && placedBlock.getType() == Material.WOOL
                && player.getGameMode() == GameMode.SURVIVAL) {
            DyeColor getWoolColor = ((Wool) placedBlock.getState().getData()).getColor();
            ItemStack wools = new Wool(getWoolColor).toItemStack(64);
            ItemStack shears = new ItemStack(Material.SHEARS, 1);

            player.getInventory().setItem(0, wools);
            player.getInventory().setItem(1, shears);


            playerRoom.setPlayersScoreboard();

            if(player.getLocation().getY() >= playerRoom.getArena().getWinHeight()) {
                playerRoom.setWinner(player);
            }
        }
    }
}
