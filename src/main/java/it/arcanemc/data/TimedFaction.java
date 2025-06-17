package it.arcanemc.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import it.arcanemc.event.TimedFactionUpdateEvent;
import it.arcanemc.manager.PermissionManager;
import it.arcanemc.util.json.interfaces.JsonSerializable;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;

@Getter
public class TimedFaction implements JsonSerializable {
    private Faction faction;
    private long timer;
    private long lastUpdate = System.currentTimeMillis();
    private ArrayList<String> availablePassNames;
    private ArrayList<String> claimedRewardNames;
    private PermissionManager permissions;

    public TimedFaction(Faction faction) {
        this.faction = faction;
        this.timer = 0L;
        this.availablePassNames = new ArrayList<>();
        this.claimedRewardNames = new ArrayList<>();
        this.permissions = new PermissionManager();
    }

    public TimedFaction(JsonObject json) {
        this.fromJson(json);
    }

    public synchronized void update(boolean isAboveThreshold) {
        long now = System.currentTimeMillis();
        if (isAboveThreshold) {
            this.timer += now - lastUpdate;
            TimedFactionUpdateEvent event = new TimedFactionUpdateEvent(this);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        lastUpdate = now;
    }


    @Override
    public JsonObject toJson() {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("faction_id", faction.getId());
        json.addProperty("timer", timer);

        JsonArray jsonAvailablePassNames = gson.toJsonTree(availablePassNames).getAsJsonArray();
        JsonArray jsonClaimedRewardNames = gson.toJsonTree(claimedRewardNames).getAsJsonArray();

        json.add("availablePassNames", jsonAvailablePassNames);
        json.add("claimedRewardNames", jsonClaimedRewardNames);

        json.add("permissions", permissions.toJson());

        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.faction = Factions.getInstance().getFactionById(json.get("faction_id").getAsString());
        this.timer = json.get("timer").getAsLong();

        Gson gson = new Gson();
        this.availablePassNames = gson.fromJson(json.getAsJsonArray("availablePassNames"),
                new TypeToken<ArrayList<String>>() {}.getType());

        this.claimedRewardNames = gson.fromJson(json.getAsJsonArray("claimedRewardNames"),
                new TypeToken<ArrayList<String>>() {}.getType());

        this.permissions = new PermissionManager();
        JsonObject permissionsJson = json.getAsJsonObject("permissions");
        if (permissionsJson != null) {
            this.permissions.fromJson(permissionsJson);
        }
    }

    public synchronized boolean unlockPass(Pass pass){
        if (this.availablePassNames.stream().noneMatch(s -> s.equalsIgnoreCase(pass.getName().toLowerCase()))){
            this.availablePassNames.add(pass.getName().toLowerCase());
            return true;
        }
        return false;
    }

    public synchronized boolean removePass(Pass pass){
        if (this.availablePassNames.stream().anyMatch(s -> s.equalsIgnoreCase(pass.getName().toLowerCase()))){
            this.availablePassNames.removeIf(s -> s.equalsIgnoreCase(pass.getName().toLowerCase()));
            return true;
        }
        return false;
    }

    public synchronized boolean claimReward(FPlayer player, Reward reward){
        if (this.claimedRewardNames.stream().noneMatch(s -> s.equalsIgnoreCase(reward.getPassRewardName()))){
            this.claimedRewardNames.add(reward.getPassRewardName());
            reward.claim(player);
            return true;
        }
        return false;
    }

    public String getAvailablePassNamesString() {
        return String.join(", ", this.availablePassNames);
    }
}
