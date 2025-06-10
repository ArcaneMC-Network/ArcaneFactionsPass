package it.arcanemc.managers;

import com.massivecraft.factions.Faction;
import it.arcanemc.configurations.ConfigurationManager;
import it.arcanemc.data.Pass;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.gui.MainGui;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class FactionPassManager {
    private PassManager passManager;
    private TimedFactionManager timedFactionManager;
    private final ConfigurationManager configs;
    private ArrayList<MainGui> guiList;
    private final String dataFolder;

    public FactionPassManager(ConfigurationManager configs, String dataFolder) {
        this.configs = configs;
        this.dataFolder = dataFolder;
        this.load();
    }

    public void load(){
        this.configs.reload();
        this.passManager = new PassManager(this.configs.get("config"));
        this.timedFactionManager = new TimedFactionManager(this.configs.get("config"), dataFolder);
        passManager.load(this.configs.get("config"));
        timedFactionManager.load();
        this.guiList = new ArrayList<>();
        timedFactionManager.get().forEach(timedFaction -> {
            MainGui mainGui = new MainGui(configs.get("gui"), configs.get("messages"), timedFaction, this.passManager);
            this.guiList.add(mainGui);
        });
    }

    public List<Pass> getDefaultPassNames(){
        return this.passManager.getPasses().keySet().stream()
                .filter(Pass::getIsDefault)
                .collect(Collectors.toList());
    }

    public void addFaction(Faction faction){
        TimedFaction timedFaction = new TimedFaction(faction);
        this.getDefaultPassNames().forEach(timedFaction::unlockPass);
        this.timedFactionManager.add(timedFaction);
        this.guiList.add(new MainGui(configs.get("gui"), configs.get("messages"), timedFaction, this.passManager));
    }

    public void removeFaction(Faction faction){
        Optional<TimedFaction> timedFactionOptional = this.timedFactionManager.get().stream()
                .filter(timedFaction -> timedFaction.getFaction().getId().equals(faction.getId()))
                .findFirst();
        timedFactionOptional.ifPresent(this.timedFactionManager::remove);
        timedFactionOptional.ifPresent(timedFaction -> {
            Optional<MainGui> optionalMainGui = this.guiList.stream()
                    .filter(mainGui -> mainGui.getTimedFaction().equals(timedFaction))
                    .findFirst();
            optionalMainGui.ifPresent(this.guiList::remove);
        });
    }

    public MainGui findMainGui(Faction faction) {
        return this.timedFactionManager.get().stream()
                .filter(timedFaction -> timedFaction.getFaction().getId().equals(faction.getId()))
                .findFirst()
                .flatMap(timedFaction ->
                        this.guiList.stream()
                                .filter(mainGui -> mainGui.getTimedFaction().equals(timedFaction))
                                .findFirst()
                )
                .orElse(null);
    }

}
