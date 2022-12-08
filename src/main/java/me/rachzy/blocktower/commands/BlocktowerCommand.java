package me.rachzy.blocktower.commands;

import me.rachzy.blocktower.files.Arenas;
import me.rachzy.blocktower.functions.ConfigPuller;
import me.rachzy.blocktower.models.ArenaModel;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlocktowerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("blocktower.usehelp")) {
                sender.sendMessage(new ConfigPuller("messages").getString("no_permission"));
                return true;
            }
            sender.sendMessage(String.format("§f================== %s§f ==================", new ConfigPuller("config").getPrefix(false)));
            sender.sendMessage("");
            sender.sendMessage("§c§lAdmin commands:");
            sender.sendMessage("§a/blocktower createarena <arena_name>§2: §7Creates an arena");
            sender.sendMessage("§a/blocktower editarena <arena_name>§2: §7Edits an existing arena by teleporting you to its world");
            sender.sendMessage("§a/blocktower setspawn <arena_name> <spawn_number>§2: §7Creates a player spawn on an arena");
            sender.sendMessage("§a/blocktower setwinheight <arena_name> <height>§2: §7Sets the height that a player have to reach to win a game");
            sender.sendMessage("§a/blocktower createroom <arena_name>§2: §7Creates a room for an arena");
            sender.sendMessage("§a/blocktower deleteroom <arena_name>§2: §7Deletes a room");
            sender.sendMessage("");
            sender.sendMessage("§e§lPlayer commands:");
            sender.sendMessage("§a/blocktower play <arena_name>§2: §7Puts the player in a queue for a room");
            sender.sendMessage("§a/blocktower leavequeue§2: §7Leaves from the current queue");
            sender.sendMessage("§a/blocktower opengui§2: §7Open the GUI that shows all the current active rooms");
            sender.sendMessage("§f====================================================");
            return true;
        }

        // Check if sender is not a player
        if ((args[0].equals("createarena")
                || args[0].equals("editarena")
                || args[0].equals("setspawn")
                || args[0].equals("setheight")
                || args[0].equals("play")
                || args[0].equals("leavequeue")
                || args[0].equals("opengui"))
                && !(sender instanceof Player)
        ) {
            sender.sendMessage(new ConfigPuller("messages").getStringWithPrefix("not_a_player"));
            return true;
        }

        Player player = (Player) sender;

        // Check if the player doesn't have permission
        if ((args[0].equals("createarena")
                || args[0].equals("editarena")
                || args[0].equals("setspawn")
                || args[0].equals("setheight")
                || args[0].equals("createroom")
                || args[0].equals("deleteroom"))
                && !sender.hasPermission("blocktower.admin")
        ) {
            sender.sendMessage(new ConfigPuller("messages").getStringWithPrefix("no_permission"));
            return true;
        }

        // Check if there is an arena with that name
        if (args[0].equals("editarena")
                || args[0].equals("setspawn")
                || args[0].equals("setwinheight")
                || args[0].equals("createroom")
                || args[0].equals("deleteroom")
                || args[0].equals("play")
        ) {
            if(args.length > 1 && Arenas.getArenaByName(args[1]) == null) {
                sender.sendMessage(new ConfigPuller("messages").getStringWithPrefix("invalid_arena_name"));
                return true;
            }
        }

        //Code chunk for createarena command
        if (args[0].equals("createarena")) {
            //If the player entered an invalid amount of args
            if (args.length != 2) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("create_arena_wrong_usage"));
                return true;
            }

            //Save the arena name
            String arenaName = args[1];

            //Create the arena and the world
            try {
                Arenas.createNewArena(arenaName);
            } catch (Exception e) {
                player.sendMessage(e.getMessage());
                return true;
            }

            WorldCreator emptyWorld = new WorldCreator(arenaName);
            emptyWorld.type(WorldType.FLAT).generatorSettings("2;0;1;");
            player.getServer().createWorld(emptyWorld);

            //Teleports the player to the world
            player.teleport(new Location(Bukkit.getWorld(arenaName), 0, 50, 0));

            //Creates a Glass Block under the player
            player.getWorld().getBlockAt(0, 48, 0).setType(Material.GLASS);

            player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("arena_created"));
            return true;
        }

        //Code chunk for editarena command
        if(args[0].equals("editarena")) {
            if(args.length != 2) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("edit_arena_wrong_usage"));
                return true;
            }
            String arenaName = args[1];
            World getArenaWorld = Bukkit.getWorld(arenaName);

            if(getArenaWorld == null) {
                getArenaWorld = Bukkit.createWorld(new WorldCreator(arenaName).type(WorldType.FLAT).generatorSettings("2;0;1;"));
            }

            player.teleport(new Location(getArenaWorld, 0, 50, 0));
            player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("edit_arena_teleport").replace("{arena_name}", arenaName));
            return true;
        }

        // Code chunk for setspawn
        if (args[0].equals("setspawn")) {
            if (args.length != 3) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_spawn_wrong_usage"));
                return true;
            }

            try {
                String arenaName = args[1];
                Integer spawnNumber = Integer.parseInt(args[2]);
                Location spawnLocation = player.getLocation().subtract(0, 1, 0);
                Block spawnBlock = spawnLocation.getBlock();

                if (spawnBlock.getType() == Material.AIR) {
                    player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_spawn_invalid_position"));
                    return true;
                }

                ArenaModel getArena = Arenas.getArenaByName(arenaName);

                getArena.createNewSpawn(spawnNumber.toString(), spawnLocation);
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("spawn_created").replace("{spawn_number}", spawnNumber.toString()));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_spawn_invalid_args"));
                return true;
            }
        }

        // Code chunk for setWinHeight
        if(args[0].equals("setwinheight")) {
            if (args.length != 3) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_win_height_wrong_usage"));
                return true;
            }

            try {
                String arenaName = args[1];
                ArenaModel arena = Arenas.getArenaByName(arenaName);
                int height = Integer.parseInt(args[2]);

                if(height < 0 || height > 256) {
                    player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_win_height_invalid_height"));
                    return true;
                }

                arena.setWinHeight(height);
                player.sendMessage(new ConfigPuller("messages")
                        .getStringWithPrefix("set_win_height_success")
                        .replace("{arena_name}", arenaName)
                        .replace("{height}", Integer.toString(height)));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(new ConfigPuller("messages").getStringWithPrefix("set_win_height_wrong_args"));
                return true;
            }
        }



        sender.sendMessage(new ConfigPuller("messages").getString("invalid_command"));
        return true;
    }
}