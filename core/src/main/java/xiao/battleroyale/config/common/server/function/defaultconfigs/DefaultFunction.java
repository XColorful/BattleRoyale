package xiao.battleroyale.config.common.server.function.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.config.common.server.function.FunctionConfigManager.FunctionConfig;
import xiao.battleroyale.config.common.server.function.type.RegisterEntry;
import xiao.battleroyale.config.common.server.function.type.RegisterEntry.RegisterDetail;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultFunction {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray functionConfigJson = new JsonArray();
        functionConfigJson.add(generateDefaultFunctionConfig0());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), functionConfigJson);
    }

    private static JsonObject generateDefaultFunctionConfig0() {
        RegisterEntry registerEntry = new RegisterEntry(true,
                Arrays.asList(
                        new RegisterDetail("battleroyale:test_function", null, CustomEventType.GAME_START_FINISH_EVENT.getName())
                )
        );

        FunctionConfig functionConfig = new FunctionConfig(0, "Default function", "#FFFFFF", registerEntry);

        return functionConfig.toJson();
    }
}