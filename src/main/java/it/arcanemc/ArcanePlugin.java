package it.arcanemc;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import it.arcanemc.configuration.ConfigurationManager;
import it.arcanemc.configuration.DependencyManager;
import it.arcanemc.configuration.ListenerManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

@Getter
public abstract class ArcanePlugin extends JavaPlugin {
    protected String prefix = "";
    protected DependencyManager dependencyManager;
    protected ConfigurationManager configurationManager;
    protected ListenerManager listenerManager;
    private LiteCommands<CommandSender> liteCommands;


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
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }
    }

    public void performInitialization(){
        // This method should be overridden by subclasses to perform any additional initialization.
    }

    public List<String> getDependencyNames(){
        // This method should be overridden by subclasses to return the names of required dependencies.
        return List.of();
    }

    public List<String> getConfigPaths(){
        // This method should be overridden by subclasses to return the paths of configuration files.
        return List.of("config.yml");
    }

    public List<Listener> getListeners(){
        // This method should be overridden by subclasses to return the list of event listeners.
        return List.of();
    }

    public Object[] getCommands() {
        // This method can be overridden by subclasses to return a list of commands if needed.
        return List.of().toArray();
    }

    public void loadCommands(){
        this.liteCommands = LiteBukkitFactory.builder(prefix,this).commands(
                getCommands()
        ).build();
    }
}
