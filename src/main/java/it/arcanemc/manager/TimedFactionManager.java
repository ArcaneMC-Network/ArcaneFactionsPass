package it.arcanemc.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.util.json.interfaces.JsonSerializable;
import it.arcanemc.util.json.JsonHandler;
import it.arcanemc.util.json.interfaces.ReadJsonSerializable;
import it.arcanemc.util.json.interfaces.WriteJsonSerializable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;

public class TimedFactionManager extends BukkitRunnable implements JsonSerializable, ReadJsonSerializable, WriteJsonSerializable {
    private final FileConfiguration config;
    private final ArrayList<TimedFaction> timedFactions;
    private final JsonHandler jsonHandler;

    public TimedFactionManager(FileConfiguration config, String path) {
        this.config = config;
        this.timedFactions = new ArrayList<>();
        this.jsonHandler = new JsonHandler(path, "timed_factions.json");
        this.load();
        this.sort();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray factionsArray = new JsonArray();
        for (TimedFaction timedFaction : timedFactions) {
            factionsArray.add(timedFaction.toJson());
        }
        json.add("factions", factionsArray);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        timedFactions.clear();
        JsonArray factionsArray = json.getAsJsonArray("factions");
        if (factionsArray != null) {
            for (JsonElement element : factionsArray) {
                JsonObject factionJson = element.getAsJsonObject();
                TimedFaction timedFaction = new TimedFaction(factionJson);
                timedFactions.add(timedFaction);
            }
        }
    }

    public void load(){
        this.fromJson(jsonHandler.loadJson());
    }

    public void save(){
        jsonHandler.saveJson(this.toJson());
    }

    public void add(TimedFaction t){
        this.timedFactions.add(t);
        this.save();
    }

    public void remove(TimedFaction t){
        this.timedFactions.remove(t);
        this.save();
    }

    public ArrayList<TimedFaction> get(){
        return this.timedFactions;
    }

    public void sort(){
        this.timedFactions.sort(Comparator.comparingLong(TimedFaction::getTimer).reversed());
    }

    @Override
    public void run() {
        for (TimedFaction faction : timedFactions) {
            int onlineCount = faction.getFaction().getOnlinePlayers().size();
            boolean isAboveThreshold = onlineCount >= this.config.getInt("settings.min-player-to-trigger");
            faction.update(isAboveThreshold);
        }
        this.save();
    }

    public void start(long intervalTicks, Plugin plugin) {
        try {
            this.cancel();
        } catch (IllegalStateException e) {
            // Ignore if the task was not running
        }

        this.runTaskTimer(plugin, 0L, intervalTicks);
    }

    public void stop() {
        this.cancel();
    }
}
