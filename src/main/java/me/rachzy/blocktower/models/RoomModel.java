package me.rachzy.blocktower.models;

import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomModel {
    final private ArenaModel arena;
    private List<Player> playerList = new ArrayList<>();
    private RoomStatus roomStatus = RoomStatus.OPEN;
    private Thread countdownThread;
    private Player winner;

    public RoomModel(ArenaModel arena) {
        this.arena = arena;
    }

    public String getName() {
        return this.arena.getName();
    }

    public RoomStatus getRoomStatus() {
        return this.roomStatus;
    }

    public ArenaModel getArena() {
        return this.arena;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Integer getCurrentPlayersAmount() {
        return this.getPlayerList().toArray().length;
    }

    public Boolean isOpen() {
        return this.roomStatus == RoomStatus.OPEN;
    }

    public void broadcastMessage(String message) {
        playerList.forEach(playerInRoom -> {
            playerInRoom.sendMessage(message);
        });
    }

    public void broadcastSound(Sound sound) {
        playerList.forEach(playerInRoom -> {
            playerInRoom.playSound(playerInRoom.getLocation(), sound, 3.0F, 0.5F);
        });
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

        Rooms.get()
                .stream()
                .filter(room -> room.getPlayerByUuid(player.getUniqueId()) != null)
                .findFirst().ifPresent(room -> room.removePlayer(player));

        playerList.add(player);
        player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("play_success"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 3.0F, 0.5F);

        this.broadcastMessage(new ConfigPuller("messages")
                .getString("new_player_on_queue")
                .replace("{player_name}", player.getDisplayName())
                .replace("{current_players}", String.valueOf(this.getCurrentPlayersAmount()))
                .replace("{total_slots}", String.valueOf(this.arena.getSlotAmount()))
        );

        if(this.getCurrentPlayersAmount() == this.getArena().getSlotAmount()) {
            this.setRoomStatus(RoomStatus.FULL);
        }

        if(this.getCurrentPlayersAmount() == this.getArena().getSlotAmount() || this.getCurrentPlayersAmount() >= 3) {
            this.startGameCounter(20);
        }
    }

    public Player getPlayerByUuid(UUID uuid) {
        Player player = this.getPlayerList()
                .stream()
                .filter(playerInRoom -> playerInRoom.getUniqueId() == uuid)
                .findFirst()
                .orElse(null);
        return player;
    }

    public void removePlayer(Player player) {
        this.playerList = this.getPlayerList()
                .stream()
                .filter(playerInRoom ->
                        playerInRoom.getUniqueId() != player.getUniqueId()).collect(Collectors.toList()
                );
        player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("leavequeue_success"));
        this.broadcastMessage(new ConfigPuller("messages")
                .getString("player_left_queue")
                .replace("{player_name}", player.getDisplayName())
                .replace("{current_players}", String.valueOf(this.getCurrentPlayersAmount()))
                .replace("{total_slots}", String.valueOf(this.arena.getSlotAmount()))
        );

        this.setRoomStatus(RoomStatus.OPEN);

        if(this.getCurrentPlayersAmount() != this.getArena().getSlotAmount() && this.getCurrentPlayersAmount() < 3) {
            this.countdownThread.interrupt();
        }
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void startGameCounter(Integer seconds) {
        this.countdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = seconds; i >= 0; i--) {
                        Thread.sleep(1000);
                        if(i == 30 || i == 15 || i == 10 || (i <= 5 && i != 0)) {
                            broadcastMessage(new ConfigPuller("messages")
                                    .getString("game_starting")
                                    .replace("{time_in_seconds}", String.valueOf(i))
                            );
                            broadcastSound(Sound.ORB_PICKUP);
                        }
                    }
                    startGame();
                } catch (InterruptedException e) {
                    //
                }
            }
        });
        this.countdownThread.start();
    }

    public void startGame() {
        // Set the room status
        this.setRoomStatus(RoomStatus.ONGAME);

        // Teleport players
        int playerIndex = 0;
        for(Player player : this.getPlayerList()) {
            int getX = (int) this.getArena().getSpawnById(playerIndex).get("x");
            int getY = (int) this.getArena().getSpawnById(playerIndex).get("y");
            int getZ = (int) this.getArena().getSpawnById(playerIndex).get("z");
            World getArenaWorld = Bukkit.getWorld(this.getName());

            Location spawnLocation = new Location(getArenaWorld, getX, getY, getZ);

            player.teleport(spawnLocation);
            playerIndex++;
        }

        if(this.countdownThread != null && this.countdownThread.isAlive()) {
            this.countdownThread.interrupt();
        }
    }
}
