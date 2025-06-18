package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.api.game.spawn.SpawnConfigTag;
import xiao.battleroyale.config.common.game.spawn.type.TeleportEntry;
import xiao.battleroyale.config.common.game.spawn.type.PlaneEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
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
        writeJsonToFile(Paths.get(GameConfigManager.get().getSpawnConfigPath(), DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static JsonObject generateDefaultSpawnConfig0() {
        JsonObject config = new JsonObject();
        config.addProperty(SpawnConfigTag.SPAWN_ID, 0);
        config.addProperty(SpawnConfigTag.SPAWN_NAME, "Random ground spawn");
        config.addProperty(SpawnConfigTag.SPAWN_COLOR, "#FFFFFFAA");

        TeleportEntry grondEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new TeleportEntry.DetailInfo(new ArrayList<>(), false, true, 0)
        );

        config.add(SpawnConfigTag.SPAWN_ENTRY, grondEntry.toJson());
        return config;
    }

    private static JsonObject generateDefaultSpawnConfig1() {
        JsonObject config = new JsonObject();
        config.addProperty(SpawnConfigTag.SPAWN_ID, 1);
        config.addProperty(SpawnConfigTag.SPAWN_NAME, "Fixed ground spawn");
        config.addProperty(SpawnConfigTag.SPAWN_COLOR, "#FFFFFFAA");

        TeleportEntry grondEntry = new TeleportEntry(SpawnShapeType.SQUARE, new Vec3(0, -60, 0), new Vec3(128, 0, 128),
                CommonDetailType.FIXED,
                new TeleportEntry.DetailInfo(Arrays.asList(
                        new Vec3(0,-60,0),
                        new Vec3(-50,-60, -50),
                        new Vec3(50, -60, -50),
                        new Vec3(50, -60, -50),
                        new Vec3(-50, -60, 50)),
                        true, true, 0)
        );

        config.add(SpawnConfigTag.SPAWN_ENTRY, grondEntry.toJson());
        return config;
    }

    private static JsonObject generateDefaultSpawnConfig2() {
        JsonObject config = new JsonObject();
        config.addProperty(SpawnConfigTag.SPAWN_ID, 2);
        config.addProperty(SpawnConfigTag.SPAWN_NAME, "Plane spawn (Not implemented)"); // TODO Plane spawn 配置
        config.addProperty(SpawnConfigTag.SPAWN_COLOR, "#FFFFFF");

        PlaneEntry planeEntry = new PlaneEntry(SpawnShapeType.SQUARE, new Vec3(0, 65, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new PlaneEntry.DetailInfo(255, 2.5, true));

        config.add(SpawnConfigTag.SPAWN_ENTRY, planeEntry.toJson());
        return config;
    }
}
