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

public class AirSpawn {

    private static final String DEFAULT_FILE_NAME = "example_airSpawn.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray spawnConfigJson = new JsonArray();
        add8000x8000Circle(spawnConfigJson);
        add8000x8000Square(spawnConfigJson);
        add5340x5340Circle(spawnConfigJson);
        add881x881Circle(spawnConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    public static JsonObject addCircle(int id, int border, int radius, int percentageHundred,
                                       int height, int simulationCount) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, height, 0), new Vec3(radius, 0, radius),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, false, 8, 20 * 15,
                        simulationCount, 0, false, true, percentageHundred / 100D, true)
        );

        SpawnConfigManager.SpawnConfig spawnConfig = new SpawnConfigManager.SpawnConfig(id, String.format("%sx%s Double center grid distribution %s * %s%%", border, border, radius, percentageHundred), "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }

    public static JsonObject addSquare(int id, int border, int side, int percentageHundred,
                                       int height, int simulationCount) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, height, 0), new Vec3(side, 0, side),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, false, 8, 20 * 15,
                        simulationCount, 0, false, true, percentageHundred / 100D, true)
        );

        SpawnConfigManager.SpawnConfig spawnConfig = new SpawnConfigManager.SpawnConfig(id, String.format("%sx%s Grid distribution %s * %s%%", border, border, side, percentageHundred), "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }

    private static void add8000x8000Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(0, 8000, 4000, 90,
                320 + 64, 64));
    }

    private static void add8000x8000Square(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addSquare(1, 8000, 4000, 90,
                320 + 64, 100));
    }

    private static void add5340x5340Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(2, 5340, 5340 / 2, 90,
                255 + 64, 32));
    }

    private static void add881x881Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(3, 881, 881 / 2, 90,
                128 + 64, 16));
    }
}
