package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.spawn.type.TeleportEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.zone.defaultconfigs.CFHC1000x1000Zone.INITIAL_BORDER_RADIUS;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.CFHC1000x1000Zone.TOTAL_GAME_PHASE;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CFHC1000x1000Spawn {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_1000x1000.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray spawnConfigJson = new JsonArray();
        spawnConfigJson.add(addGoldenSpiral(0, (int) INITIAL_BORDER_RADIUS, (double) (TOTAL_GAME_PHASE - 1) /TOTAL_GAME_PHASE, 64, 8));
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    public static JsonObject addGoldenSpiral(int id, int radius, double percentage,
                                             int height, int additionalSimulation) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, height, 0), new Vec3(radius, 0, radius),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, true, 8, 20 * 5,
                        additionalSimulation, 0.2, true, true, percentage, true));
        SpawnConfig spawnConfig = new SpawnConfig(id, String.format("Golden Spiral %s x %s (%2d%%)", radius * 2, radius * 2, (int) (percentage * 100)), "#FFFFFFAA",
                0, groundEntry);
        return spawnConfig.toJson();
    }
}
