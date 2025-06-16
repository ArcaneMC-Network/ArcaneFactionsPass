package it.arcanemc.manager;

import it.arcanemc.ArcanePlugin;
import it.arcanemc.gui.MainGui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainGuiManager {
    private List<MainGui> guis;

    public MainGuiManager(ArcanePlugin plugin) {
        this.load(plugin);
    }

    public void load(ArcanePlugin plugin) {
        this.guis = new ArrayList<>();
    }

    public void add(MainGui gui) {
        this.guis.add(gui);
    }

    public void remove(MainGui gui) {
        this.guis.remove(gui);
    }

    public Stream<MainGui> stream() {
        return this.guis.stream();
    }
}
