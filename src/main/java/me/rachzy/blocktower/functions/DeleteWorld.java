package me.rachzy.blocktower.functions;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

public class DeleteWorld {
    private void delete(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    this.delete(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
    }
    public DeleteWorld(World world) {
        Bukkit.getServer().unloadWorld(world, false);
        File path = world.getWorldFolder();

        this.delete(path);
    }
}
