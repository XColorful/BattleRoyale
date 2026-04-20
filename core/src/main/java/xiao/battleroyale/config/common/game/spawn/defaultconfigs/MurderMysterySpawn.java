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

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class MurderMysterySpawn {

    private static final String DEFAULT_FILE_NAME = "example_murdermystery256x256_circle.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray spawnConfigJson = new JsonArray();
        add256x256Circle(spawnConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static void add256x256Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addDistributedCircleExact(0, 256, 256 / 2, 90));
    }

    public static JsonObject addDistributedCircleExact(int id, int border, int radius, int percentageHundred) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, -60, 0), new Vec3(radius, 0, radius),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(false, true, 0, 20 * 15,
                        1, 1, true, true, percentageHundred / 100D, true)
        );

        SpawnConfigManager.SpawnConfig spawnConfig = new SpawnConfigManager.SpawnConfig(id, String.format("%sx%s Golden spiral distribution %s * %s%%", border, border, radius, percentageHundred), "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }
}
