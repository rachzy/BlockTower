package me.rachzy.blocktower.models;

import me.rachzy.blocktower.BlockTower;
import me.rachzy.blocktower.data.Rooms;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.functions.CopyWorld;
import me.rachzy.blocktower.functions.DeleteWorld;
import me.rachzy.blocktower.types.RoomStatus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomModel {
    final private ArenaModel arena;
    private List<RoomPlayerModel> playerList = new ArrayList<>();
    private RoomStatus roomStatus = RoomStatus.OPEN;
    private Thread countdownThread;
    private Thread playerSpawnThread;
    private Thread startGameThread;
    private Thread fireworksThread;
    private Thread endGameThread;
    private boolean gameStarted = false;
    private Player winner = null;

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
        List<Player> pList = new ArrayList<>();
        playerList.forEach(player -> pList.add(player.getPlayer()));
        return pList;
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

    public Integer getCurrentPlayersAmount() {
        return this.getPlayerList().toArray().length;
    }

    public List<Player> getStandingPlayers() {
        return this.getPlayerList()
                .stream()
                .filter(player -> this.getRoomPlayer(player).getLives() > 0)
                .collect(Collectors.toList());
    }
    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setWinner(Player player) {
        if (this.winner != null) return;
        this.winner = player;

        // Sets the PvP to false
        player.getWorld().setPVP(false);

        this.getPlayerList().forEach(playerInRoom -> {
            if (playerInRoom.getUniqueId() == player.getUniqueId()) {
                playerInRoom.sendTitle(new ConfigPuller("messages").getString("victory_title"), new ConfigPuller("messages").getString("victory_subtitle"));
            } else {
                playerInRoom.sendTitle(new ConfigPuller("messages").getString("defeat_title"), new ConfigPuller("messages").getString("defeat_subtitle"));
            }
        });
        this.broadcastMessage(new ConfigPuller("messages").getStringWithPrefix("victory_message").replace("{player_name}", player.getDisplayName()));

        // Thread that will remove every single player
        this.endGameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        endGame();
                    }
                }.runTask(BlockTower.getPlugin(BlockTower.class));
            }
        });

        // Thread that will spawn fireworks near the players
        this.fireworksThread = new Thread(new Runnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        getPlayerList().forEach((playerInRoom) -> {
                            Location playerLocation = playerInRoom.getLocation();
                            World playerWorld = playerInRoom.getWorld();

                            playerWorld.spawnEntity(playerLocation, EntityType.FIREWORK);
                            playerWorld.spawnEntity(playerLocation, EntityType.FIREWORK);
                            playerWorld.spawnEntity(playerLocation, EntityType.FIREWORK);
                        });
                    }
                }.runTask(BlockTower.getPlugin(BlockTower.class));
                fireworksThread.interrupt();
            }
        });

        // Counter thread
        this.countdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fireworksThread.start();
                    Thread.sleep(5000);
                    endGameThread.start();
                    countdownThread.interrupt();
                } catch (InterruptedException e) {
                    //
                }
            }

        });
        this.countdownThread.start();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
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

    public void removePlayer(Player player) {
        String messageName = "player_left_queue";
        if (this.getRoomStatus() == RoomStatus.ONGAME) {
            messageName = "player_left_game";
            Location storedPlayerLocation = this.getRoomPlayer(player).getStoredLocation();
            ItemStack[] storedPlayerInventoryItems = this.getRoomPlayer(player).getStoredInventoryItems();

            // Clear the player's scoreboard
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            player.teleport(storedPlayerLocation);
            player.getInventory().setContents(storedPlayerInventoryItems);
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);

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

        if (this.getCurrentPlayersAmount() != this.getArena().getSlotAmount()
                && this.getCurrentPlayersAmount() < 3
                && this.countdownThread != null
                && this.countdownThread.isAlive()
        ) {
            this.countdownThread.interrupt();
        }

        if (this.getRoomStatus() == RoomStatus.ONGAME) {
            if(this.getCurrentPlayersAmount() == 1) {
                this.setWinner(this.getStandingPlayers().stream().findFirst().orElse(null));
            }
            if(this.getCurrentPlayersAmount() == 0) {
                this.endGame();
            }
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

    public void setPlayersScoreboard() {
        this.getPlayerList().forEach(player -> {

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();

            Objective objective = scoreboard.registerNewObjective("GameScoreBoard", "dummy");
            objective.setDisplayName(new ConfigPuller("config").getPrefix(false));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<Player> playersOrderedByHeight = this.getPlayerList()
                    .stream()
                    .sorted((p1, p2) -> (int) (p1.getLocation().getY() - p2.getLocation().getY()))
                    .collect(Collectors.toList());

            Score line1 = objective.getScore("\u0020");
            line1.setScore(1);

            Score remainingLives = objective.getScore(
                    new ConfigPuller("messages")
                            .getString("scoreboard_remaining_lives")
                            .replace("{player_lives}", String.valueOf(this.getRoomPlayer(player).getLives()))
            );
            remainingLives.setScore(2);

            Score kills = objective.getScore(
                    new ConfigPuller("messages")
                            .getString("scoreboard_kills")
                            .replace("{player_kills}", String.valueOf(this.getRoomPlayer(player).getKills()))
            );
            kills.setScore(3);

            Score line4 = objective.getScore("\u0020");
            line4.setScore(4);

            String[] colorsList = new String[]{
                    "a", "c", "d", "e"
            };

            playersOrderedByHeight.forEach(playerInRoom -> {
                if (player.getGameMode() != GameMode.SURVIVAL && this.getRoomPlayer(player).getLives() <= 0) return;

                int indexOfPlayer = playersOrderedByHeight.indexOf(playerInRoom);
                if (indexOfPlayer >= 4) return;

                Score top = objective.getScore
                        (String.format("§%s%s. %s §6(%s)",
                                colorsList[indexOfPlayer],
                                indexOfPlayer + 1,
                                playerInRoom.getDisplayName(),
                                Math.round(playerInRoom.getLocation().getY()))
                        );
                top.setScore(8 - indexOfPlayer);
            });

            Score line9 = objective.getScore("\u0020\u0020");
            line9.setScore(9);

            Score winHeight = objective.getScore(
                    new ConfigPuller("messages")
                            .getString("scoreboard_win_height")
                            .replace("{win_height}", String.valueOf(this.arena.getWinHeight()))
            );
            winHeight.setScore(10);

            Score currentHeight = objective.getScore(
                    new ConfigPuller("messages")
                            .getString("scoreboard_current_height")
                            .replace("{current_height}", String.valueOf(Math.round(player.getLocation().getY())))
            );
            currentHeight.setScore(11);


            Score line12 = objective.getScore("\u0020\u0020\u0020");
            line12.setScore(12);

            player.setScoreboard(scoreboard);
        });
    }

    public void startGame() {
        this.countdownThread.interrupt();
        // To avoid bugs
        if (this.getRoomStatus() == RoomStatus.ONGAME) return;

        // Sets the room status
        this.setRoomStatus(RoomStatus.ONGAME);

        // Creates a copy of the original arena world
        World getArenaWorld = Bukkit.getWorld(this.getName());
        String arenaCopyName = String.format("%s_game", this.getName());

        new CopyWorld(getArenaWorld, arenaCopyName);

        World arenaCopyWorld = Bukkit.getWorld(arenaCopyName);
        arenaCopyWorld.setPVP(true);

        // Teleports players
        int playerIndex = 0;
        for (Player player : this.getPlayerList()) {
            int getX = (int) this.getArena().getSpawnById(playerIndex).get("x");
            int getY = (int) this.getArena().getSpawnById(playerIndex).get("y");
            int getZ = (int) this.getArena().getSpawnById(playerIndex).get("z");

            RoomPlayerModel roomPlayer = this.getRoomPlayer(player);
            Location spawnLocation = new Location(arenaCopyWorld, getX, getY, getZ);

            roomPlayer.setSpawnLocation(spawnLocation);
            roomPlayer.setStoredInventoryItems(player.getInventory().getContents());
            roomPlayer.setStoredLocation(player.getLocation());

            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawnLocation);
            player.getInventory().clear();

            // Gives player's items
            Block playerBlock = player.getLocation().subtract(0, 1, 0).getBlock();

            if (playerBlock.getType() != Material.WOOL) {
                playerBlock.setType(Material.WOOL);
            }

            DyeColor getWoolColor = ((Wool) playerBlock.getState().getData()).getColor();
            ItemStack wools = new Wool(getWoolColor).toItemStack(64);
            player.getInventory().setItem(0, wools);

            // Sets the wool color as the player color
            this.getRoomPlayer(player).setColor(getWoolColor.getColor());

            // Sets the player's armor
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) helmet.getItemMeta();
            armorMeta.setColor(getWoolColor.getColor());
            helmet.setItemMeta(armorMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            chestplate.setItemMeta(armorMeta);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            leggings.setItemMeta(armorMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            boots.setItemMeta(armorMeta);

            ItemStack[] armor = new ItemStack[] {boots, leggings, chestplate, helmet};
            player.getInventory().setArmorContents(armor);

            // Gives player shears
            ItemStack shears = new ItemStack(Material.SHEARS);
            player.getInventory().setItem(1, shears);

            playerIndex++;
        }

        // Interrupts the counter
        if (this.startGameThread != null && this.startGameThread.isAlive()) {
            this.startGameThread.interrupt();
        }

        // Starts the 5 seconds freezetime
        this.startUnfreezeCounter();

        // Set players scoreboards
        this.setPlayersScoreboard();
    }

    public void endGame() {
        this.getPlayerList().forEach(this::removePlayer);
        this.setGameStarted(false);
        this.winner = null;
        this.setRoomStatus(RoomStatus.RESETTING);

        //Deletes the copy world
        String arenaCopyName = String.format("%s_game", this.getName());
        World copyWorld = Bukkit.getWorld(arenaCopyName);
        if (copyWorld != null) {
            new DeleteWorld(copyWorld);
        }

        this.setRoomStatus(RoomStatus.OPEN);
    }

    public void playerDeath(Player player) {
        RoomPlayerModel roomPlayer = this.getRoomPlayer(player);
        roomPlayer.decreaseLives();

        this.broadcastMessage(new ConfigPuller("messages")
                .getStringWithPrefix("player_died")
                .replace("{player_name}", player.getDisplayName())
        );

        this.playerSpawn(player);
    }

    public void playerKilled(Player player, Player killer) {
        RoomPlayerModel roomPlayer = this.getRoomPlayer(player);
        RoomPlayerModel roomKiller = this.getRoomPlayer(killer);

        roomPlayer.decreaseLives();
        roomKiller.increaseKills();
        roomPlayer.setLastDamageReceived(null);

        killer.sendMessage(new ConfigPuller("messages").getStringWithPrefix("player_kill"));
        this.broadcastMessage(new ConfigPuller("messages")
                .getStringWithPrefix("player_killed")
                .replace("{player_name}", player.getDisplayName())
                .replace("{killer_name}", killer.getDisplayName())
        );

        player.setGameMode(GameMode.SPECTATOR);
        playerSpawn(player);
    }

    public void playerSpawn(Player player) {
        RoomPlayerModel roomPlayer = this.getRoomPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);

        if (roomPlayer.getLives() <= 0) {
            this.broadcastMessage(new ConfigPuller("messages")
                    .getStringWithPrefix("player_eliminated")
                    .replace("{player_name}", player.getDisplayName())
            );
            player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("player_lost"));
            player.teleport(roomPlayer.getSpawnLocation());
            this.setPlayersScoreboard();

            if(this.getStandingPlayers().toArray().length == 1) {
                Player winner = this.getStandingPlayers().stream().findFirst().orElse(null);
                this.setWinner(winner);
            }
            return;
        }

        //Cleans the spawnpoint
        double spawnX = roomPlayer.getSpawnLocation().getX();
        double spawnY = roomPlayer.getSpawnLocation().getY();
        double spawnZ = roomPlayer.getSpawnLocation().getZ();
        Block firstBlockAbove = new Location(Bukkit.getWorld(this.getName() + "_game"), spawnX, spawnY + 1, spawnZ).getBlock();
        Block secondBlockAbove = new Location(Bukkit.getWorld(this.getName()  + "_game"), spawnX, spawnY + 2, spawnZ).getBlock();
        firstBlockAbove.setType(Material.AIR);
        secondBlockAbove.setType(Material.AIR);

        player.teleport(firstBlockAbove.getLocation());
        player.setGameMode(GameMode.SURVIVAL);

        this.setPlayersScoreboard();
    }
}
