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
import java.util.ArrayList;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultSpawn {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray spawnConfigJson = new JsonArray();
        spawnConfigJson.add(generateDefaultSpawnConfig0());
        spawnConfigJson.add(generateDefaultSpawnConfig1());
        spawnConfigJson.add(generateDefaultSpawnConfig2());
        writeJsonToFile(Paths.get(String.valueOf(SpawnConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static JsonObject generateDefaultSpawnConfig0() {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new TeleportDetailEntry(new ArrayList<>(), false, true, 0, 20 * 15)
        );

        SpawnConfig spawnConfig = new SpawnConfig(0, "Random ground spawn", "#FFFFFFAA", -1, true,
                groundEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateDefaultSpawnConfig1() {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.FIXED,
                new TeleportDetailEntry(Arrays.asList(
                        new Vec3(0, -60, 0),
                        new Vec3(-50, -60, -50),
                        new Vec3(-50, -60, 50),
                        new Vec3(50, -60, -50),
                        new Vec3(50, -60, 50)),
                        true, true, 0, 20 * 15)
        );

        SpawnConfig spawnConfig = new SpawnConfig(1, "Fixed ground spawn", "#FFFFFFAA",
                groundEntry);

        return spawnConfig.toJson();
    }

    private static JsonObject generateDefaultSpawnConfig2() {
        PlaneEntry planeEntry = new PlaneEntry(SpawnShapeType.SQUARE, new Vec3(0, 65, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new PlaneDetailEntry(255, 2.5, true));

        SpawnConfig spawnConfig = new SpawnConfig(2, "Plane spawn (Not implemented)", "#FFFFFF",
                planeEntry);

        return spawnConfig.toJson();
    }
}
