package it.arcanemc.event;

import it.arcanemc.data.TimedFaction;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimedFactionUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final TimedFaction timedFaction;

    public TimedFactionUpdateEvent(TimedFaction timedFaction) {
        this.timedFaction = timedFaction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
