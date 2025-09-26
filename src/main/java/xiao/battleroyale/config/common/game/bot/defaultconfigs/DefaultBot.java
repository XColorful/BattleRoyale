package xiao.battleroyale.config.common.game.bot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager.BotConfig;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultBot {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray botConfigJson = new JsonArray();
        botConfigJson.add(generateDefaultBotConfig0());
        writeJsonToFile(Paths.get(GameConfigManager.get().getConfigDirPath(BotConfigManager.get().getNameKey()), DEFAULT_FILE_NAME).toString(), botConfigJson);
    }

    private static JsonObject generateDefaultBotConfig0() {
        BotConfig botConfig = new BotConfig(0, "Not implemented yet", "#FFFFFF", true, null);
        return botConfig.toJson();
    }
}
