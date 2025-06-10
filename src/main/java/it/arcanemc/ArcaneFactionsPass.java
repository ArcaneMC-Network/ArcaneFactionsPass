package it.arcanemc;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import it.arcanemc.command.FPassCommand;
import it.arcanemc.listener.FactionListener;
import it.arcanemc.manager.FactionPassManager;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public final class ArcaneFactionsPass extends ArcanePlugin {
    public final String PREFIX = "arcanefactionspass";

    @Getter
    private FactionPassManager factionPassManager;


    public List<String> getDependencyNames(){
        return new ArrayList<>(
                List.of(
                        "Factions"
                )
        );
    }

    public List<String> getConfigPaths(){
        return List.of(
                "config.yml",
                "messages.yml",
                "gui.yml"
        );
    }

    public List<Listener> getListeners(){
        return List.of(
                new FactionListener(this.factionPassManager)
        );
    }

    public void loadCommands(){
        LiteBukkitFactory.builder(PREFIX,this).commands(
            new FPassCommand(this.factionPassManager)
        ).build();
    }

    @Override
    public void performInitialization() {
        this.factionPassManager = new FactionPassManager(this);
    }
}
