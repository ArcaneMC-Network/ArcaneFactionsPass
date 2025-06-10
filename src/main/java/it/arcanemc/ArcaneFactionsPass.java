package it.arcanemc;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import it.arcanemc.commands.FPassCommand;
import it.arcanemc.configurations.ConfigurationManager;
import it.arcanemc.configurations.DependencyManager;
import it.arcanemc.configurations.ListenerManager;
import it.arcanemc.listeners.FactionListener;
import it.arcanemc.managers.FactionPassManager;
import it.arcanemc.utils.Timer;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ArcaneFactionsPass extends JavaPlugin {
    public String PREFIX = "arcanefactionspass";

    private DependencyManager dependencyManager;

    @Getter
    private ConfigurationManager configurationManager;
    @Getter
    private ListenerManager listenerManager;
    @Getter
    private FactionPassManager factionPassManager;


    @Override
    public void onEnable() {
        this.dependencyManager = new DependencyManager();
        this.getDependencyNames().forEach(d -> this.dependencyManager.add(d));
        this.dependencyManager.validate();

        this.configurationManager = new ConfigurationManager(this);
        this.listenerManager = new ListenerManager(this);

        this.getConfigPaths().forEach(path -> this.configurationManager.add(path));

        this.factionPassManager = new FactionPassManager(this.configurationManager, getDataFolder().getAbsolutePath());
        this.factionPassManager.getTimedFactionManager().start(Timer.convertVerbose(this.configurationManager.get("config").getString("settings.update")), this);

        this.getListeners().forEach(listener -> this.listenerManager.add(listener));

        this.loadCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private List<String> getDependencyNames(){
        return new ArrayList<>(
                List.of(
                        "Factions"
                )
        );
    }

    private List<String> getConfigPaths(){
        return new ArrayList<>(
                List.of(
                        "config.yml",
                        "messages.yml",
                        "gui.yml"
                )
        );
    }

    private List<Listener> getListeners(){
        return new ArrayList<>(
                List.of(
                        new FactionListener(this.factionPassManager)
                )
        );
    }

    public void loadCommands(){
        LiteBukkitFactory.builder(PREFIX,this).commands(
            new FPassCommand(this.factionPassManager)
        ).build();
    }
}
