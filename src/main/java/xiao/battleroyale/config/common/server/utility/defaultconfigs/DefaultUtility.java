package xiao.battleroyale.config.common.server.utility.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.server.ServerConfigManager;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultUtility {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray utilityConfigJson = new JsonArray();
        utilityConfigJson.add(generateDefaultUtilityConfig0());
        writeJsonToFile(Paths.get(ServerConfigManager.get().getUtilityConfigPath(), DEFAULT_FILE_NAME).toString(), utilityConfigJson);
    }

    private static JsonObject generateDefaultUtilityConfig0() {
        return new JsonObject();
    }
}
