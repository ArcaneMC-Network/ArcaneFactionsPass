package it.arcanemc.configuration;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Getter
public class ListenerManager {
    private final Plugin plugin;

    public ListenerManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void add(Listener listener){
        this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
    }
}
