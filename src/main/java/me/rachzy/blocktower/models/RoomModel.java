package me.rachzy.blocktower.models;

import me.rachzy.blocktower.functions.ConfigPuller;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomModel {
    final private ArenaModel arena;
    private final List<Player> playerList = new ArrayList<>();
    private final Boolean isStarted = false;
    private Player winner;

    public RoomModel(ArenaModel arena) {
        this.arena = arena;
    }

    public String getName() {
        return this.arena.getName();
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Integer getCurrentPlayersAmount() {
        return this.getPlayerList().toArray().length;
    }

    public void addPlayer(Player player) throws Exception {
        Player checkIfPlayerIsAlreadyInTheRoom = playerList
                .stream()
                .filter(playerInRoom -> playerInRoom.getUniqueId() == player.getUniqueId())
                .findFirst()
                .orElse(null);

        if (checkIfPlayerIsAlreadyInTheRoom != null) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("play_player_already_in_room"));
        }

        if(this.getCurrentPlayersAmount() >= this.arena.getSlotAmount()) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("full_room"));
        }

        playerList.add(player);
        player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("play_success"));

        playerList.forEach(playerInRoom -> {
            playerInRoom.sendMessage(
                    new ConfigPuller("messages")
                            .getString("play_new_player_on_queue")
                            .replace("{player_name}", player.getDisplayName())
                            .replace("{current_players}", String.valueOf(this.getCurrentPlayersAmount()))
                            .replace("{total_slots}", String.valueOf(this.arena.getSlotAmount()))
            );
        });
    }
}
