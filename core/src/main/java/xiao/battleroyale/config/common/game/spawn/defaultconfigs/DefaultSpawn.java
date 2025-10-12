package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.spawn.type.TeleportEntry;
import xiao.battleroyale.config.common.game.spawn.type.PlaneEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.PlaneDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultSpawn {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray spawnConfigJson = new JsonArray();
        spawnConfigJson.add(generateRandomTeleport0());
        spawnConfigJson.add(generateFixedTeleport1());
        spawnConfigJson.add(generateGridDistribution2());
        spawnConfigJson.add(generateDoubleCenterDistribution3());
        spawnConfigJson.add(generateGoldenSpiralDistribution4());
        spawnConfigJson.add(generatePlaneSpawn5());
        writeJsonToFile(Paths.get(String.valueOf(SpawnConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static JsonObject generateRandomTeleport0() {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new TeleportDetailEntry(false, true, 0, 20 * 15)
        );

        SpawnConfig spawnConfig = new SpawnConfig(0, "Random ground spawn", "#FFFFFFAA", -1, true,
                groundEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateFixedTeleport1() {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.FIXED,
                new TeleportDetailEntry(true, true, 0, 20 * 15,
                        Arrays.asList(
                        new Vec3(0, -60, 0),
                        new Vec3(-50, -60, -50),
                        new Vec3(-50, -60, 50),
                        new Vec3(50, -60, -50),
                        new Vec3(50, -60, 50)), false)
        );

        SpawnConfig spawnConfig = new SpawnConfig(1, "Fixed ground spawn", "#FFFFFFAA",
                groundEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateGridDistribution2() {
        TeleportEntry teleportEntry = new TeleportEntry(SpawnShapeType.RECTANGLE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, false, 10, 20 * 15,
                        15, 0, false, true, 0.8, false));

        SpawnConfig spawnConfig = new SpawnConfig(2, "Grid distributed teleport", "#FFFFFFAA",
                teleportEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateDoubleCenterDistribution3() {
        TeleportEntry teleportEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, -60 + 255, 0), new Vec3(128, 0, 128),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, false, 0, 20 * 15,
                        15, 0, false, true, 0.8, true));

        SpawnConfig spawnConfig = new SpawnConfig(3, "Double center grid distribution teleport", "#FFFFFFAA",
                teleportEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateGoldenSpiralDistribution4() {
        TeleportEntry teleportEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, -60 + 255, 0), new Vec3(128, 0, 128),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, false, 0, 20 * 15,
                        15, 0, true, true, 0.8, false));

        SpawnConfig spawnConfig = new SpawnConfig(4, "Golden Spiral Distribution teleport", "#FFFFFFAA",
                teleportEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generatePlaneSpawn5() {
        PlaneEntry planeEntry = new PlaneEntry(SpawnShapeType.SQUARE, new Vec3(0, 65, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new PlaneDetailEntry(255, 2.5, true));

        SpawnConfig spawnConfig = new SpawnConfig(2, "Plane spawn (Not implemented)", "#FFFFFF",
                planeEntry);

        return spawnConfig.toJson();
    }
}
