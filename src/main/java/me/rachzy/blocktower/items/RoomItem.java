package me.rachzy.blocktower.items;

import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.RoomModel;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RoomItem {
    public static ItemStack get(RoomStatus roomStatus, RoomModel room) {
        ItemStack openRoomItem = new ItemStack(Material.matchMaterial(new ConfigPuller("config").getString(roomStatus.toString().toLowerCase() + "_room_item")));
        ItemMeta openRoomItemMeta = openRoomItem.getItemMeta();

        openRoomItemMeta.setDisplayName(String.format("§a%s", room.getName()));
        List<String> lore = new ArrayList<>();
        new ConfigPuller("config").getList(roomStatus.toString().toLowerCase() + "_room_item_lore").forEach(line -> {
            lore.add(line
                    .replace("{current_players}", room.getCurrentPlayersAmount().toString())
                    .replace("{total_slots}", room.getArena().getSlotAmount().toString())
            );
        });
        openRoomItemMeta.setLore(lore);

        openRoomItem.setItemMeta(openRoomItemMeta);
        return openRoomItem;
    }
}
