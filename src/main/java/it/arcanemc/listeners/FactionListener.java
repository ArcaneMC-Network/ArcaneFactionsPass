package it.arcanemc.listeners;

import com.massivecraft.factions.event.*;
import it.arcanemc.managers.FactionPassManager;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Getter
public class FactionListener implements Listener {
    private final FactionPassManager factionPassManager;

    public FactionListener(FactionPassManager factionPassManager) {
        this.factionPassManager = factionPassManager;
    }

    @EventHandler
    public void onFactionCreate(FactionCreateEvent event) {
        this.factionPassManager.addFaction(event.getFaction());
    }

    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        this.factionPassManager.removeFaction(event.getFaction());
    }

    @EventHandler
    public void onFactionAutoDisband(FactionAutoDisbandEvent event) {
        this.factionPassManager.removeFaction(event.getFaction());
    }
}
