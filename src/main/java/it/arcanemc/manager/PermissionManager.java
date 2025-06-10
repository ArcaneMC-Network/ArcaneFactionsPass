package it.arcanemc.manager;

import com.google.gson.JsonObject;
import it.arcanemc.util.json.interfaces.JsonSerializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionManager implements JsonSerializable {
    private boolean recruit;
    private boolean member;
    private boolean moderator;
    private boolean coleader;

    public PermissionManager() {
        this.coleader = false;
        this.moderator = false;
        this.member = false;
        this.recruit = false;
    }

    public boolean get(String role){
        switch(role.toLowerCase()){
            case "recruit":
                return this.recruit;
            case "member":
                return this.member;
            case "moderator":
                return this.moderator;
            case "coleader":
                return this.coleader;
            case "admin":
                return true;
        }
        throw new IllegalArgumentException(String.format("%s does not exist as role", role));
    }

    public boolean get(int value) {
        switch(value){
            case 0:
                return this.recruit;
            case 1:
                return this.member;
            case 2:
                return this.moderator;
            case 3:
                return this.coleader;
        }
        throw new IllegalArgumentException(String.format("%s does not exist as role", value));
    }

    public boolean toggle(String role){
        switch(role.toLowerCase()){
            case "recruit":
                this.recruit = !this.recruit;
                return recruit;
            case "member":
                this.member = !this.member;
                return member;
            case "moderator":
                this.moderator = !this.moderator;
                return moderator;
            case "coleader":
                this.coleader = !this.coleader;
                return coleader;
        }
        throw new IllegalArgumentException(String.format("%s does not exist as role", role));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("recruit", this.recruit);
        json.addProperty("member", this.member);
        json.addProperty("moderator", this.moderator);
        json.addProperty("coleader", this.coleader);
        return json;
    }

    @Override
    public void fromJson(JsonObject json) {
        if (json.has("recruit")) {
            this.recruit = json.get("recruit").getAsBoolean();
        }
        if (json.has("member")) {
            this.member = json.get("member").getAsBoolean();
        }
        if (json.has("moderator")) {
            this.moderator = json.get("moderator").getAsBoolean();
        }
        if (json.has("coleader")) {
            this.coleader = json.get("coleader").getAsBoolean();
        }
    }
}
