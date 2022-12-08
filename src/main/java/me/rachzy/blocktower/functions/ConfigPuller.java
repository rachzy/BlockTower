package me.rachzy.blocktower.functions;

import me.rachzy.blocktower.BlockTower;
import me.rachzy.blocktower.files.Messages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

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
        return mainConfig.getString("plugin_prefix").concat(withArrows ? " §7§l>> " : "");
    }

    public String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(key));
    }

    public String getStringWithPrefix(String key) {
        return getPrefix(true).concat(ChatColor.translateAlternateColorCodes('&', config.getString(key)));
    }
}
