package it.arcanemc.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class ConfigurationManager {
    private final Plugin plugin;

    private HashMap<String, FileConfiguration> configurations;

    public ConfigurationManager(Plugin plugin) {
        this.plugin = plugin;
        this.configurations = new HashMap<>();
    }

    public void add(String filePath){
        File messagesFile = new File(plugin.getDataFolder(), filePath);
        if (!messagesFile.exists()) {
            plugin.saveResource(filePath, false);
        }
        this.configurations.put(filePath.split("\\.")[0], YamlConfiguration.loadConfiguration(messagesFile));
    }

    public FileConfiguration get(String key){
        return this.configurations.get(key);
    }

    public void reload(){
        var oldKeys = new HashMap<>(this.configurations);
        this.configurations = new HashMap<>();

        for (String key : oldKeys.keySet()) {
            String filePath = key + ".yml";
            add(filePath);
        }
    }
}
