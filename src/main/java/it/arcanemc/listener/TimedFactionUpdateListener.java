package it.arcanemc.listener;

import it.arcanemc.event.TimedFactionUpdateEvent;
import it.arcanemc.gui.MainGui;
import it.arcanemc.manager.FactionPassManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TimedFactionUpdateListener implements Listener {
    private final FactionPassManager factionPassManager;

    public TimedFactionUpdateListener(FactionPassManager factionPassManager) {
        this.factionPassManager = factionPassManager;
    }

    @EventHandler
    public void onTimedFactionUpdate(TimedFactionUpdateEvent event) {
        MainGui mainGui = this.factionPassManager.findMainGui(event.getTimedFaction().getFaction());
        if (mainGui.isViewing()) {
            mainGui.update();
        }
    }
}
