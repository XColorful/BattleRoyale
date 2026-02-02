package xiao.battleroyale.config.common.server.profile.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.config.common.server.profile.ConfigProfileEntryTag;
import xiao.battleroyale.api.config.common.server.profile.IConfigProfileEntry;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigApplicationEntry implements IConfigProfileEntry {

    public final List<ConfigManagerEntry> configManagers;
    public final List<ConfigSubManagerEntry> configSubManagers;

    public ConfigApplicationEntry(List<ConfigManagerEntry> configManagerEntries, List<ConfigSubManagerEntry> configSubManagerEntries) {
        this.configManagers = new ArrayList<>(configManagerEntries);
        this.configSubManagers = new ArrayList<>(configSubManagerEntries);
    }
    @Override public @NotNull ConfigApplicationEntry copy() {
        return new ConfigApplicationEntry(this.configManagers, this.configSubManagers);
    }

    @Override
    public String getType() {
        return "configApplicationEntry";
    }

    @Override
    public int applyConfigProfile() {
        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        int appliedTotal = 0;

        for (ConfigManagerEntry managerEntry : this.configManagers) {
            @Nullable IConfigManager configManager = modConfigManager.getConfigManager(managerEntry.nameKey);
            if (configManager == null) continue;

            for (ConfigSubManagerEntry subEntry : managerEntry.configSubManagers) {
                @Nullable IConfigSubManager<?> subManager = configManager.getConfigSubManager(subEntry.nameKey);
                appliedTotal += applySubConfigProfile(subManager, subEntry.configEntries.asList());
            }
        }

        for (ConfigSubManagerEntry subEntry : this.configSubManagers) {
            @Nullable IConfigSubManager<?> subManager = modConfigManager.getConfigSubManager(subEntry.nameKey);
            appliedTotal += applySubConfigProfile(subManager, subEntry.configEntries.asList());
        }
        return appliedTotal;
    }

    protected int applySubConfigProfile(@Nullable IConfigSubManager<?> subManager, List<ConfigEntry> entries) {
        if (subManager == null || entries.isEmpty()) return 0;

        int count = 0;
        for (ConfigEntry entry : entries) {
            if (subManager.switchConfigFile(entry.folderId, entry.fileName)) {
                IConfigSingleEntry configSingleEntry = subManager.getConfigEntry(entry.folderId, entry.id);
                if (configSingleEntry != null) {
                    configSingleEntry.applyDefault();
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject managerJson = new JsonObject();
        configManagers.forEach(m -> m.addToJson(managerJson));

        JsonObject subManagerJson = new JsonObject();
        configSubManagers.forEach(s -> s.addToJson(subManagerJson));

        jsonObject.add(ConfigProfileEntryTag.CONFIG_MANAGER, managerJson);
        jsonObject.add(ConfigProfileEntryTag.CONFIG_SUB_MANAGER, subManagerJson);
        return jsonObject;
    }

    @Nullable
    public static ConfigApplicationEntry fromJson(JsonObject jsonObject) {
        List<ConfigManagerEntry> managers = new ArrayList<>();
        List<ConfigSubManagerEntry> subManagers = new ArrayList<>();

        JsonObject mObj = JsonUtils.getJsonObject(jsonObject, ConfigProfileEntryTag.CONFIG_MANAGER, null);
        if (mObj != null) {
            for (Map.Entry<String, JsonElement> entry : mObj.entrySet()) {
                managers.add(ConfigManagerEntry.fromJson(entry.getKey(), entry.getValue().getAsJsonObject()));
            }
        }

        JsonObject sObj = JsonUtils.getJsonObject(jsonObject, ConfigProfileEntryTag.CONFIG_SUB_MANAGER, null);
        if (sObj != null) {
            for (Map.Entry<String, JsonElement> entry : sObj.entrySet()) {
                subManagers.add(ConfigSubManagerEntry.fromJson(entry.getKey(), entry.getValue().getAsJsonObject()));
            }
        }

        return new ConfigApplicationEntry(managers, subManagers);
    }
}
