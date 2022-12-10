package me.rachzy.blocktower.models;

import me.rachzy.blocktower.BlockTower;
import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.functions.CopyWorld;
import me.rachzy.blocktower.functions.DeleteWorld;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomModel {
    final private ArenaModel arena;
    private List<RoomPlayerModel> playerList = new ArrayList<>();
    private RoomStatus roomStatus = RoomStatus.OPEN;
    private Thread countdownThread;
    private Thread startGameThread;
    private boolean gameStarted = false;
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

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public ArenaModel getArena() {
        return this.arena;
    }

    public List<Player> getPlayerList() {
        List<Player> pList = new ArrayList<>();
        playerList.forEach(player -> pList.add(player.getPlayer()));
        return pList;
    }

    public Integer getCurrentPlayersAmount() {
        return this.getPlayerList().toArray().length;
    }

    public Boolean isOpen() {
        return this.roomStatus == RoomStatus.OPEN;
    }

    public void broadcastMessage(String message) {
        getPlayerList().forEach(playerInRoom -> {
            playerInRoom.sendMessage(message);
        });
    }

    public void broadcastSound(Sound sound) {
        getPlayerList().forEach(playerInRoom -> {
            playerInRoom.playSound(playerInRoom.getLocation(), sound, 3.0F, 0.5F);
        });
    }

    public void broadcastTitle(String title, String subtitle) {
        getPlayerList().forEach(playerInRoom -> {
            playerInRoom.sendTitle(title, subtitle);
        });
    }

    public void addPlayer(Player player) throws Exception {
        Player checkIfPlayerIsAlreadyInTheRoom = getPlayerList()
                .stream()
                .filter(playerInRoom -> playerInRoom.getUniqueId() == player.getUniqueId())
                .findFirst()
                .orElse(null);

        if (checkIfPlayerIsAlreadyInTheRoom != null) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("play_player_already_in_room"));
        }

        if (this.getCurrentPlayersAmount() >= this.arena.getSlotAmount()) {
            throw new Exception(new ConfigPuller("messages").getStringWithPrefix("full_room"));
        }

        Rooms.get()
                .stream()
                .filter(room -> room.getPlayerByUuid(player.getUniqueId()) != null)
                .findFirst().ifPresent(room -> room.removePlayer(player));

        this.playerList.add(new RoomPlayerModel(player));
        player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("play_success"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 3.0F, 0.5F);

        this.broadcastMessage(new ConfigPuller("messages")
                .getString("new_player_on_queue")
                .replace("{player_name}", player.getDisplayName())
                .replace("{current_players}", String.valueOf(this.getCurrentPlayersAmount()))
                .replace("{total_slots}", String.valueOf(this.arena.getSlotAmount()))
        );

        if (this.getCurrentPlayersAmount() == this.getArena().getSlotAmount()) {
            this.setRoomStatus(RoomStatus.FULL);
        }

        if (this.getCurrentPlayersAmount() == this.getArena().getSlotAmount() || this.getCurrentPlayersAmount() >= 3) {
            this.startGameCounter(20);
        }
    }

    public Player getPlayerByUuid(UUID uuid) {
        return this.getPlayerList()
                .stream()
                .filter(playerInRoom -> playerInRoom.getUniqueId() == uuid)
                .findFirst()
                .orElse(null);
    }

    public RoomPlayerModel getRoomPlayer(Player player) {
        return this.playerList.stream()
                .filter(roomPlayer -> roomPlayer.getPlayer().getUniqueId() == player.getUniqueId())
                .findFirst()
                .orElse(null);
    }

    public void removePlayer(Player player) {
        String messageName = "player_left_queue";
        if (this.getRoomStatus() == RoomStatus.ONGAME) {
            messageName = "player_left_game";
            Location storedPlayerLocation = this.getRoomPlayer(player).getStoredLocation();
            ItemStack[] storedPlayerInventoryItems = this.getRoomPlayer(player).getStoredInventoryItems();

            player.teleport(storedPlayerLocation);
            player.getInventory().setContents(storedPlayerInventoryItems);

            player.updateInventory();
        }

        this.playerList = this.playerList
                .stream()
                .filter(playerInRoom ->
                        playerInRoom.getPlayer().getUniqueId() != player.getUniqueId()).collect(Collectors.toList()
                );
        player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("quit_success"));

        this.broadcastMessage(new ConfigPuller("messages")
                .getString(messageName)
                .replace("{player_name}", player.getDisplayName())
                .replace("{current_players}", String.valueOf(this.getCurrentPlayersAmount()))
                .replace("{total_slots}", String.valueOf(this.arena.getSlotAmount()))
        );

        if (this.getRoomStatus() == RoomStatus.FULL) {
            this.setRoomStatus(RoomStatus.OPEN);
        }

        if(this.getRoomStatus() == RoomStatus.ONGAME && this.getCurrentPlayersAmount() == 0) {
            this.endGame();
        }

        if (this.getCurrentPlayersAmount() != this.getArena().getSlotAmount()
                && this.getCurrentPlayersAmount() < 3
                && this.countdownThread != null
                && this.countdownThread.isAlive()
        ) {
            this.countdownThread.interrupt();
        }
    }

    public void startGameCounter(Integer seconds) {
        this.startGameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                }.runTask(BlockTower.getPlugin(BlockTower.class));
            }
        });
        this.countdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = seconds; i >= 0; i--) {
                        Thread.sleep(1000);
                        // Seconds that will broadcast the remaining time
                        if (i == 30 || i == 15 || i == 10 || (i <= 5 && i != 0)) {
                            broadcastMessage(new ConfigPuller("messages")
                                    .getString("game_starting_countdown")
                                    .replace("{time_in_seconds}", String.valueOf(i))
                            );
                            broadcastSound(Sound.ORB_PICKUP);
                        }
                    }
                    startGameThread.start();
                    countdownThread.interrupt();
                } catch (InterruptedException e) {
                    //
                }
            }

        });
        this.countdownThread.start();
    }

    public void startUnfreezeCounter() {
        this.countdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 5; i >= 0; i--) {
                        Thread.sleep(1000);
                        // Seconds that will broadcast the remaining time
                        if (i == 30 || i == 15 || i == 10 || (i <= 5 && i != 0)) {
                            broadcastTitle(new ConfigPuller("config").getPrefix(false),
                                    new ConfigPuller("messages")
                                    .getString("game_freezetime_subtitle")
                                    .replace("{time_in_seconds}", String.valueOf(i))
                            );
                            broadcastSound(Sound.SUCCESSFUL_HIT);
                        }
                    }
                    setGameStarted(true);
                    broadcastTitle(new ConfigPuller("messages").getString("game_started_title"), "");
                    broadcastSound(Sound.ENDERDRAGON_GROWL);
                    countdownThread.interrupt();
                } catch (InterruptedException e) {
                    //
                }
            }

        });
        this.countdownThread.start();
    }

    public void startGame() {
        // To avoid bugs
        if (this.getRoomStatus() == RoomStatus.ONGAME) return;

        // Set the room status
        this.setRoomStatus(RoomStatus.ONGAME);

        // Teleport players
        int playerIndex = 0;
        for (Player player : this.getPlayerList()) {
            int getX = (int) this.getArena().getSpawnById(playerIndex).get("x");
            int getY = (int) this.getArena().getSpawnById(playerIndex).get("y");
            int getZ = (int) this.getArena().getSpawnById(playerIndex).get("z");

            // Creates a copy of the original arena world
            World getArenaWorld = Bukkit.getWorld(this.getName());
            String arenaCopyName = String.format("%s_game", this.getName());
            new CopyWorld(getArenaWorld, arenaCopyName);
            World arenaCopyWorld = Bukkit.getWorld(arenaCopyName);

            Location spawnLocation = new Location(arenaCopyWorld, getX, getY, getZ);

            this.getRoomPlayer(player).setStoredInventoryItems(player.getInventory().getContents());
            this.getRoomPlayer(player).setStoredLocation(player.getLocation());

            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawnLocation);
            player.getInventory().clear();

            playerIndex++;
        }

        if (this.startGameThread != null && this.startGameThread.isAlive()) {
            this.startGameThread.interrupt();
        }

        this.startUnfreezeCounter();
    }

    public void endGame() {
        this.getPlayerList().forEach(this::removePlayer);
        this.setGameStarted(false);
        this.setRoomStatus(RoomStatus.RESETTING);

        //Deletes the copy world
        String arenaCopyName = String.format("%s_game", this.getName());
        World copyWorld = Bukkit.getWorld(arenaCopyName);
        new DeleteWorld(copyWorld);

        this.setGameStarted(false);
        this.setRoomStatus(RoomStatus.OPEN);
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
