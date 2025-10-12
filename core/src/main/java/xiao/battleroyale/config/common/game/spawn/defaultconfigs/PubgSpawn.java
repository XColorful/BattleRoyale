package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.spawn.type.TeleportEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class PubgSpawn {

    private static final String DEFAULT_FILE_NAME = "example_pubg8000x8000_circle.json";

    public static void generateDefaultConfigs() {
        JsonArray spawnConfigJson = new JsonArray();
        add8000x8000Circle(spawnConfigJson);
        add8000x8000Square(spawnConfigJson);
        add5340x5340Circle(spawnConfigJson);
        add881x881Circle(spawnConfigJson);
        writeJsonToFile(Paths.get(String.valueOf(SpawnConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    public static JsonObject addCircle(int id, int border, int radius, int percentageHundred) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, -60, 0), new Vec3(radius * percentageHundred / 100D, 0, radius * percentageHundred / 100D),
                CommonDetailType.RANDOM,
                new TeleportDetailEntry(true, true, 8, 20 * 15)
        );

        SpawnConfig spawnConfig = new SpawnConfig(id, border + "x" + border + " Circle radius " + radius + " * " + percentageHundred + "%", "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }

    public static JsonObject addSquare(int id, int border, int side, int percentageHundred) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(side * percentageHundred / 100D, 0, side * percentageHundred / 100D),
                CommonDetailType.RANDOM,
                new TeleportDetailEntry(true, true, 8, 20 * 15)
        );

        SpawnConfig spawnConfig = new SpawnConfig(id, border + "x" + border + " Square side " + side + " * " + percentageHundred + "%", "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }

    private static void add8000x8000Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(0, 8000, 4000, 90));
    }

    private static void add8000x8000Square(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addSquare(1, 8000, 4000, 90));
    }

    private static void add5340x5340Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(2, 5340, 5340 / 2, 90));
    }

    private static void add881x881Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addCircle(3, 881, 881 / 2, 90));
    }
}
