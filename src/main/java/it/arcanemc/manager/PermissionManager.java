package it.arcanemc.manager;

import com.google.gson.JsonObject;
import it.arcanemc.util.json.interfaces.JsonSerializable;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

@Getter
public class PermissionManager implements JsonSerializable {
    private boolean recruit;
    private boolean member;
    private boolean moderator;
    private boolean coleader;

    private final ReentrantLock lock = new ReentrantLock();

    public PermissionManager() {
        this.recruit = false;
        this.member = false;
        this.moderator = false;
        this.coleader = false;
    }

    public boolean get(String role) {
        lock.lock();
        try {
            switch (role.toLowerCase()) {
                case "recruit": return recruit;
                case "member": return member;
                case "moderator": return moderator;
                case "coleader": return coleader;
                case "admin": return true;
                default: throw new IllegalArgumentException(role + " does not exist as role");
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean get(int value) {
        lock.lock();
        try {
            switch (value) {
                case 0: return recruit;
                case 1: return member;
                case 2: return moderator;
                case 3: return coleader;
                case 4: return true;
                default: throw new IllegalArgumentException(value + " does not exist as role");
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean toggle(String role) {
        lock.lock();
        try {
            switch (role.toLowerCase()) {
                case "recruit": recruit = !recruit; return recruit;
                case "member": member = !member; return member;
                case "moderator": moderator = !moderator; return moderator;
                case "coleader": coleader = !coleader; return coleader;
                default: throw new IllegalArgumentException(role + " does not exist as role");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public JsonObject toJson() {
        lock.lock();
        try {
            JsonObject json = new JsonObject();
            json.addProperty("recruit", recruit);
            json.addProperty("member", member);
            json.addProperty("moderator", moderator);
            json.addProperty("coleader", coleader);
            return json;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void fromJson(JsonObject json) {
        lock.lock();
        try {
            if (json.has("recruit")) recruit = json.get("recruit").getAsBoolean();
            if (json.has("member")) member = json.get("member").getAsBoolean();
            if (json.has("moderator")) moderator = json.get("moderator").getAsBoolean();
            if (json.has("coleader")) coleader = json.get("coleader").getAsBoolean();
        } finally {
            lock.unlock();
        }
    }
}
