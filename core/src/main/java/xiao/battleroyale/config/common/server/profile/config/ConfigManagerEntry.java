package xiao.battleroyale.config.common.server.profile.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManagerEntry {
    public final String nameKey;
    public final List<ConfigSubManagerEntry> configSubManagers;

    public ConfigManagerEntry(String nameKey, List<ConfigSubManagerEntry> configSubManagers) {
        this.nameKey = nameKey;
        this.configSubManagers = new ArrayList<>(configSubManagers);
    }

    public static ConfigManagerEntry fromJson(String nameKey, JsonObject jsonObject) {
        List<ConfigSubManagerEntry> subManagers = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                subManagers.add(ConfigSubManagerEntry.fromJson(entry.getKey(), entry.getValue().getAsJsonObject()));
            }
        }
        return new ConfigManagerEntry(nameKey, subManagers);
    }

    public void addToJson(JsonObject jsonObject) {
        JsonObject subObject = new JsonObject();
        configSubManagers.forEach(s -> s.addToJson(subObject));
        jsonObject.add(nameKey, subObject);
    }
}
