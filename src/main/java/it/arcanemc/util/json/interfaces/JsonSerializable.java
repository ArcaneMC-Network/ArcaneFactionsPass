package it.arcanemc.util.json.interfaces;

import com.google.gson.JsonObject;

public interface JsonSerializable {
    JsonObject toJson();
    void fromJson(JsonObject json);
}
