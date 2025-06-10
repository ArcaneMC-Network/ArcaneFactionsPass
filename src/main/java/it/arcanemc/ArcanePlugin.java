package it.arcanemc;

import it.arcanemc.configuration.ConfigurationManager;
import it.arcanemc.configuration.DependencyManager;
import it.arcanemc.configuration.ListenerManager;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

@Getter
public abstract class ArcanePlugin extends JavaPlugin {
    protected DependencyManager dependencyManager;
    protected ConfigurationManager configurationManager;
    protected ListenerManager listenerManager;

    @Override
    public void onEnable() {
        this.dependencyManager = new DependencyManager(this);
        this.getDependencyNames().forEach(d -> this.dependencyManager.add(d));
        this.dependencyManager.validate();
        this.configurationManager = new ConfigurationManager(this);
        this.listenerManager = new ListenerManager(this);
        this.getConfigPaths().forEach(path -> this.configurationManager.add(path));

        this.performInitialization();

        this.getListeners().forEach(listener -> this.listenerManager.add(listener));
        this.loadCommands();

    }

    @Override
    public void onDisable() {}

    public abstract List<String> getDependencyNames();

    public abstract List<String> getConfigPaths();

    public abstract List<Listener> getListeners();

    public abstract void loadCommands();

    public abstract void performInitialization();
}
