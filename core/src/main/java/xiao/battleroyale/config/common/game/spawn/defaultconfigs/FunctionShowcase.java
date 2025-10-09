package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.type.TeleportEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class FunctionShowcase {

    private static final String DEFAULT_FILE_NAME = "example_function_showcase.json";

    public static void generateDefaultConfigs() {
        JsonArray spawnConfigJson = new JsonArray();
        spawnConfigJson.add(generateDefaultSpawnConfig0());
        writeJsonToFile(Paths.get(String.valueOf(SpawnConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }


    private static JsonObject generateDefaultSpawnConfig0() {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.FIXED,
                new TeleportDetailEntry(Arrays.asList(
                        new Vec3(2032, 53, -9060),
                        new Vec3(1954, 53, -9038)),
                        true, true, 0, 20 * 15)
        );

        SpawnConfigManager.SpawnConfig spawnConfig = new SpawnConfigManager.SpawnConfig(1, "Fixed ground spawn", "#FFFFFFAA",
                groundEntry);

        return spawnConfig.toJson();
    }
}
