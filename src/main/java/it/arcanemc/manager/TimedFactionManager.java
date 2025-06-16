package it.arcanemc.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import it.arcanemc.ArcanePlugin;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.util.Timer;
import it.arcanemc.util.json.interfaces.JsonSerializable;
import it.arcanemc.util.json.JsonHandler;
import it.arcanemc.util.json.interfaces.ReadJsonSerializable;
import it.arcanemc.util.json.interfaces.WriteJsonSerializable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimedFactionManager implements JsonSerializable, ReadJsonSerializable, WriteJsonSerializable {
    private final ArcanePlugin plugin;
    private final List<TimedFaction> timedFactions;
    private final JsonHandler jsonHandler;
    private BukkitRunnable task;

    public TimedFactionManager(ArcanePlugin plugin) {
        this.plugin = plugin;
        this.timedFactions = new ArrayList<>();
        this.jsonHandler = new JsonHandler(
                this.plugin.getDataFolder().getAbsolutePath(),
                "timed_factions.json"
        );
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

    public void syncFromFactions() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal()) {
                continue;
            }
            boolean exists = timedFactions.stream()
                    .anyMatch(timedFaction -> timedFaction.getFaction().getId().equals(faction.getId()));
            if (!exists) {
                TimedFaction timedFaction = new TimedFaction(faction);
                this.timedFactions.add(timedFaction);
            }
        }
    }

    public void load(){
        this.fromJson(jsonHandler.loadJson());
        this.syncFromFactions();
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

    public List<TimedFaction> get(){
        return this.timedFactions;
    }

    public void sort(){
        this.timedFactions.sort(Comparator.comparingLong(TimedFaction::getTimer).reversed());
    }

    public void start() {
        this.stop();

        FileConfiguration config = this.plugin.getConfigurationManager().get("config");
        long period = Timer.convertVerbose(config.getString("settings.update")) / 50L;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                for (TimedFaction faction : timedFactions) {
                    int onlineCount = faction.getFaction().getOnlinePlayers().size();
                    boolean isAboveThreshold = onlineCount >= config.getInt("settings.min-player-to-trigger");
                    faction.update(isAboveThreshold);
                }
                save();
            }
        };

        this.task.runTaskTimer(this.plugin, 0L, period);
    }

    public void stop() {
        if (this.task != null && this.task.getTaskId() != -1) {
            this.task.cancel();
            this.task = null;
        }
    }
}
