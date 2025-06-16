package it.arcanemc;

import it.arcanemc.command.FPassCommand;
import it.arcanemc.listener.CommandListener;
import it.arcanemc.listener.FactionListener;
import it.arcanemc.listener.TimedFactionUpdateListener;
import it.arcanemc.manager.FactionPassManager;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public final class ArcaneFactionsPass extends ArcanePlugin {
    @Getter
    private FactionPassManager factionPassManager;

    @Override
    public void performInitialization() {
        this.prefix = "ArcaneFactionsPass";
        this.factionPassManager = new FactionPassManager(this);
    }

    @Override
    public List<String> getDependencyNames(){
        return List.of(
            "Factions"
        );
    }

    @Override
    public List<String> getConfigPaths() {
        List<String> paths = new ArrayList<>(super.getConfigPaths());
        paths.addAll(
                List.of(
                        "message.yml",
                        "gui.yml",
                        "sound.yml"
                )
        );
        return paths;
    }

    @Override
    public List<Listener> getListeners(){
        return List.of(
                new FactionListener(this.factionPassManager),
                new CommandListener(this.factionPassManager),
                new TimedFactionUpdateListener(this.factionPassManager)
        );
    }

    @Override
    public Object[] getCommands(){
        return List.of(
                new FPassCommand(this.factionPassManager)
        ).toArray();
    }
}
