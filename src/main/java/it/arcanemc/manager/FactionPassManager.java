package it.arcanemc.manager;

import com.massivecraft.factions.Faction;
import it.arcanemc.ArcanePlugin;
import it.arcanemc.configuration.ConfigurationManager;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.gui.MainGui;
import lombok.Getter;

import java.util.Optional;

@Getter
public class FactionPassManager {
    private final ArcanePlugin plugin;
    private PassManager passManager;
    private TimedFactionManager timedFactionManager;
    private MainGuiManager guiManager;

    public FactionPassManager(ArcanePlugin plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load(){
        if (this.timedFactionManager != null){
            this.timedFactionManager.stop();
        }

        ConfigurationManager configs = this.plugin.getConfigurationManager();
        configs.reload();
        this.passManager = new PassManager(configs.get("config"));
        this.passManager.load(configs.get("config"));
        this.timedFactionManager = new TimedFactionManager(this.plugin);
        this.passManager.getDefaultPasses().forEach(defaultPass -> {
            this.timedFactionManager.get().forEach(timedFaction -> timedFaction.unlockPass(defaultPass));
        });
        this.guiManager = new MainGuiManager(this.plugin);
        timedFactionManager.get().forEach(timedFaction -> {
            MainGui mainGui = new MainGui(configs.get("gui"), configs.get("message"), configs.get("sound"), timedFaction, this.passManager);
            this.guiManager.add(mainGui);
        });
        this.timedFactionManager.start();
    }

    public void addFaction(Faction faction){
        ConfigurationManager configs = this.plugin.getConfigurationManager();
        TimedFaction timedFaction = new TimedFaction(faction);
        this.passManager.getDefaultPasses().forEach(timedFaction::unlockPass);
        this.timedFactionManager.add(timedFaction);
        this.guiManager.add(new MainGui(configs.get("gui"), configs.get("message"), configs.get("sound"), timedFaction, this.passManager));
    }

    public void removeFaction(Faction faction){
        Optional<TimedFaction> timedFactionOptional = this.timedFactionManager.get().stream()
                .filter(timedFaction -> timedFaction.getFaction().getId().equals(faction.getId()))
                .findFirst();
        timedFactionOptional.ifPresent(this.timedFactionManager::remove);
        timedFactionOptional.ifPresent(timedFaction -> {
            Optional<MainGui> optionalMainGui = this.guiManager.stream()
                    .filter(mainGui -> mainGui.getTimedFaction().equals(timedFaction))
                    .findFirst();
            optionalMainGui.ifPresent(this.guiManager::remove);
        });
    }

    public MainGui findMainGui(Faction faction) {
        return this.timedFactionManager.get().stream()
                .filter(timedFaction -> timedFaction.getFaction().getId().equals(faction.getId()))
                .findFirst()
                .flatMap(timedFaction ->
                        this.guiManager.stream()
                                .filter(mainGui -> mainGui.getTimedFaction().equals(timedFaction))
                                .findFirst()
                )
                .orElse(null);
    }
}
