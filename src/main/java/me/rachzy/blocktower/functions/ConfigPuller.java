package me.rachzy.blocktower.functions;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.rachzy.blocktower.BlockTower;
import me.rachzy.blocktower.files.Messages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigPuller {
    public static FileConfiguration config;
    public static FileConfiguration mainConfig = BlockTower.getPlugin(BlockTower.class).getConfig();

    public ConfigPuller(String fileName) {
        switch(fileName) {
            case "messages":
                config = Messages.get();
                break;
            default:
                config = mainConfig;
        }
    }

    public String getPrefix(Boolean withArrows) {
        return ChatColor.translateAlternateColorCodes('&', mainConfig.getString("plugin_prefix").concat(withArrows ? " §7§l>> " : ""));
    }

    public String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(key));
    }

    public String getStringWithPrefix(String key) {
        return getPrefix(true).concat(ChatColor.translateAlternateColorCodes('&', config.getString(key)));
    }

    public List<String> getList(String key) {
        List<String> listWithColors = new ArrayList<>();

        for(String s : config.getStringList(key)) {
            listWithColors.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return listWithColors;
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
}
