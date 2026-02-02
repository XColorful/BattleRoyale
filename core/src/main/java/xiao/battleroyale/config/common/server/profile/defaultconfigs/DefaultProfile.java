package xiao.battleroyale.config.common.server.profile.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.config.common.server.profile.ProfileConfigManager.ProfileConfig;
import xiao.battleroyale.config.common.server.profile.config.ConfigApplicationEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigManagerEntry;
import xiao.battleroyale.config.common.server.profile.config.ConfigSubManagerEntry;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultProfile {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray profileConfigJson = new JsonArray();
        profileConfigJson.add(generateDefaultProfileConfig0());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), profileConfigJson);
    }

    public static JsonObject generateDefaultProfileConfig0() {
        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();

        List<ConfigManagerEntry> configManagerEntries = new ArrayList<>();
        for (IConfigManager configManager : modConfigManager.getConfigManagers()) {
            configManagerEntries.add(buildConfigManagerEntry(configManager));
        }

        List<ConfigSubManagerEntry> configSubManagerEntries = new ArrayList<>();
        for (IConfigSubManager<?> configSubManager : modConfigManager.getConfigSubManagers()) {
            configSubManagerEntries.add(buildConfigSubManagerEntry(configSubManager));
        }

        ConfigApplicationEntry configApplicationEntry = new ConfigApplicationEntry(configManagerEntries, configSubManagerEntries);

        ProfileConfig profileConfig = new ProfileConfig(0, "Default profile", "#FFFFFF", configApplicationEntry);
        return profileConfig.toJson();
    }

    public static ConfigManagerEntry buildConfigManagerEntry(IConfigManager configManager) {
        List<ConfigSubManagerEntry> configSubManagerEntries = new ArrayList<>();
        for (IConfigSubManager<?> configSubManager : configManager.getConfigSubManagers()) {
            configSubManagerEntries.add(buildConfigSubManagerEntry(configSubManager));
        }
        return new ConfigManagerEntry(configManager.getNameKey(), configSubManagerEntries);
    }

    public static ConfigSubManagerEntry buildConfigSubManagerEntry(IConfigSubManager<?> configSubManager) {
        List<ConfigEntry> configEntries = new ArrayList<>();
        for (Integer folderId : configSubManager.getAllFolderId()) {
            configEntries.add(new ConfigEntry(folderId, "example", 0));
        }
        return new ConfigSubManagerEntry(configSubManager.getNameKey(), configEntries);
    }
}
