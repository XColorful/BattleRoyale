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

public class DeathMatchSpawn {

    private static final String DEFAULT_FILE_NAME = "example_deathmatch100x100_circle.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray spawnConfigJson = new JsonArray();
        add100x100Circle(spawnConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), spawnConfigJson);
    }

    private static void add100x100Circle(JsonArray spawnConfigJson) {
        spawnConfigJson.add(addDistributedCircle(0, 100, 100 / 2, 90));
    }

    public static JsonObject addDistributedCircle(int id, int border, int radius, int percentageHundred) {
        TeleportEntry groundEntry = new TeleportEntry(SpawnShapeType.CIRCLE, new Vec3(0, -60, 0), new Vec3(radius, 0, radius),
                CommonDetailType.DISTRIBUTED,
                new TeleportDetailEntry(true, true, 5, 20 * 15,
                        4, 1, true, true, percentageHundred / 100D, true)
        );

        SpawnConfigManager.SpawnConfig spawnConfig = new SpawnConfigManager.SpawnConfig(id, String.format("%sx%s Golden spiral distribution %s * %s%%", border, border, radius, percentageHundred), "#FFFFFFAA",
                0, groundEntry);

        return spawnConfig.toJson();
    }
}
