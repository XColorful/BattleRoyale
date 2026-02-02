package xiao.battleroyale.config.common.server.profile.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigSubManagerEntry {
    public final String nameKey;
    public final ClassUtils.ArrayMap<Integer, ConfigEntry> configEntries = new ClassUtils.ArrayMap<>(ConfigEntry::getFolderId);

    public ConfigSubManagerEntry(String nameKey, List<ConfigEntry> configEntries) {
        this.nameKey = nameKey;
        this.configEntries.addAll(configEntries);
    }

    // 终究还是夹在ConfigManagerEntry和ConfigEntry的中层承担了一切
    // 总控：同构分形+透明，维持真空
    // 上层：追求熵下移，拒绝负责，全量委派
    // 中层：被迫熵不下移，同时受两头压迫
    // 下层：自己处理不了问题，追求熵上移，期望躺平
    public static ConfigSubManagerEntry fromJson(String nameKey, JsonObject jsonObject) {
        List<ConfigEntry> entries = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            try {
                int folderId = Integer.parseInt(entry.getKey());
                ConfigEntry configEntry = ConfigEntry.fromJson(folderId, entry.getValue().getAsJsonObject());
                if (configEntry != null) entries.add(configEntry);
            } catch (NumberFormatException ignored) {
                BattleRoyale.LOGGER.warn("ConfigSubManagerEntry: Skipped invalid ConfigEntry, nameKey: {}, jsonObject: {}", nameKey, jsonObject);
            }
        }
        return new ConfigSubManagerEntry(nameKey, entries);
    }

    public void addToJson(JsonObject jsonObject) {
        JsonObject subObject = new JsonObject();
        for (ConfigEntry entry : configEntries.asList()) {
            entry.addToJson(subObject);
        }
        jsonObject.add(nameKey, subObject);
    }
}
