package it.arcanemc;

import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.message.MessageKey;
import it.arcanemc.command.FPassCommand;
import it.arcanemc.listener.CommandListener;
import it.arcanemc.listener.FactionListener;
import it.arcanemc.listener.TimedFactionUpdateListener;
import it.arcanemc.manager.FactionPassManager;
import it.arcanemc.util.Colors;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<MessageKey<?>, String> getCommandMessages() {
        return Map.of(
                LiteMessages.INVALID_USAGE,
                Colors.translate(this.configurationManager.get("message").getString("errors.invalid-command"))
        );
    }
}
