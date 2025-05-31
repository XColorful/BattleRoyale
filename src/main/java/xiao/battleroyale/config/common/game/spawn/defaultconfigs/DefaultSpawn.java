package xiao.battleroyale.config.common.game.spawn.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.api.game.spawn.SpawnConfigTag;
import xiao.battleroyale.config.common.game.spawn.type.GroundEntry;
import xiao.battleroyale.config.common.game.spawn.type.PlaneEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.nio.file.Paths;
import java.util.ArrayList;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultSpawn {

    private static final String DEFAULT_FILE_NAME = "default.json";

    public static void generateDefaultConfigs() {
        JsonArray spawnConfigJson = new JsonArray();
        spawnConfigJson.add(generateDefaultSpawnConfig1());
        spawnConfigJson.add(generateDefaultSpawnConfig2());
        writeJsonToFile(Paths.get(GameConfigManager.GAME_CONFIG_PATH, SpawnConfigManager.SPAWN_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static JsonObject generateDefaultSpawnConfig1() {
        JsonObject config = new JsonObject();
        config.addProperty(SpawnConfigTag.SPAWN_ID, 1);
        config.addProperty(SpawnConfigTag.SPAWN_NAME, "Ground");
        config.addProperty(SpawnConfigTag.SPAWN_COLOR, "#FFFFFF");

        GroundEntry grondEntry = new GroundEntry(SpawnShapeType.SQUARE, new Vec3(0, 65, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new GroundEntry.DetailInfo(new ArrayList<>(), true, true, 0)
        );

        config.add(SpawnConfigTag.SPAWN_ENTRY, grondEntry.toJson());
        return config;
    }

    private static JsonObject generateDefaultSpawnConfig2() {
        JsonObject config = new JsonObject();
        config.addProperty(SpawnConfigTag.SPAWN_ID, 2);
        config.addProperty(SpawnConfigTag.SPAWN_NAME, "Plane");
        config.addProperty(SpawnConfigTag.SPAWN_COLOR, "#FFFFFF");

        PlaneEntry planeEntry = new PlaneEntry(SpawnShapeType.SQUARE, new Vec3(0, 65, 0), new Vec3(128, 0, 128),
                CommonDetailType.RANDOM,
                new PlaneEntry.DetailInfo(255, 2.5, true));

        config.add(SpawnConfigTag.SPAWN_ENTRY, planeEntry.toJson());
        return config;
    }
}
