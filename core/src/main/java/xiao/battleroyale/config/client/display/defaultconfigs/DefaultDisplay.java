package xiao.battleroyale.config.client.display.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.client.display.DisplayConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager.DisplayConfig;
import xiao.battleroyale.config.client.display.type.GameEntry;
import xiao.battleroyale.config.client.display.type.MapEntry;
import xiao.battleroyale.config.client.display.type.TeamEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultDisplay {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray displayConfigJson = new JsonArray();
        displayConfigJson.add(generateDefaultDisplayConfig0());
        displayConfigJson.add(generateDefaultDisplayConfig1());
        displayConfigJson.add(generateDefaultDisplayConfig2());
        writeJsonToFile(Paths.get(String.valueOf(DisplayConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), displayConfigJson);
    }

    private static JsonObject generateDefaultDisplayConfig0() {
        TeamEntry teamEntry = new TeamEntry(true, -0.9, -0.9, 20 * 8);
        GameEntry gameEntry = new GameEntry(true, 0.85, 0.9, "#FFFFFFFF", "#00FFFFFF");
        MapEntry mapEntry = new MapEntry();

        DisplayConfig displayConfig = new DisplayConfig(0, "All display", "#FFFFFFAA", teamEntry, gameEntry, mapEntry);

        return displayConfig.toJson();
    }

    private static JsonObject generateDefaultDisplayConfig1() {
        TeamEntry teamEntry = new TeamEntry(false);
        GameEntry gameEntry = new GameEntry(false);
        MapEntry mapEntry = new MapEntry(false);

        DisplayConfig displayConfig = new DisplayConfig(1, "Display nothing", "#FFFFFFAA", teamEntry, gameEntry, mapEntry);

        return displayConfig.toJson();
    }

    private static JsonObject generateDefaultDisplayConfig2() {
        TeamEntry teamEntry = new TeamEntry(false);
        GameEntry gameEntry = new GameEntry(true, 0.85, 0.9, "#FFFFFFFF", "#00FFFFFF");
        MapEntry mapEntry = new MapEntry();

        DisplayConfig displayConfig = new DisplayConfig(2, "Hide team", "#FFFFFFAA", teamEntry, gameEntry, mapEntry);

        return displayConfig.toJson();
    }
}