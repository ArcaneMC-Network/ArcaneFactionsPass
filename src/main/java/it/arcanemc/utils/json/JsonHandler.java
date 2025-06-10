package it.arcanemc.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHandler {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;

    public JsonHandler(String pluginFolderPath, String dataFolderPath, String dataFileName) {
        File dataFolder = new File(pluginFolderPath, dataFolderPath);

        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdir();
            if (!created) {
                throw new RuntimeException("Error creating the 'data' folder");
            }
        }

        this.file = new File(dataFolder, dataFileName);
        this.createFile();
    }

    public JsonHandler(String pluginFolderPath, String dataFileName) {
        this(pluginFolderPath, "data", dataFileName);
    }

    public JsonHandler(String pluginFolderPath) {
        this(pluginFolderPath, "data.json");
    }

    public void createFile(){
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(new JsonObject(), writer);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error creating the JSON file", e);
            }
        }
    }

    public JsonObject loadJson() {
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Error loading the JSON file", e);
        }
    }

    public void saveJson(JsonObject jsonObject) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error saving the JSON file", e);
        }
    }
}
