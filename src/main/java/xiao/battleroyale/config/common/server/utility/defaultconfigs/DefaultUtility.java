package xiao.battleroyale.config.common.server.utility.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager.UtilityConfig;
import xiao.battleroyale.config.common.server.utility.type.SurvivalEntry;

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
        SurvivalEntry survivalEntry = new SurvivalEntry("minecraft:overworld", false,
                new Vec3(0, 70, 0), new Vec3(8, 160, 8), false, false,
                true, true);

        UtilityConfig utilityConfig = new UtilityConfig(0, "Overworld Survival", "#FFFFFF", true, survivalEntry);
        return utilityConfig.toJson();
    }
}
