package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.SafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class UHC1000x1000Simple {

    private static final String DEFAULT_FILE_NAME = "example_UHC_1000x1000_50minutes_simple.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateShrinkingBorder(
                1000, // 初始边长
                50 * 60 * 20, // 50分钟 * 20tick
                25)); // 结束边长
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    public static JsonObject generateShrinkingBorder(int side, int shrinkTime, int endSide) {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, shrinkTime, 20, 0, 1.0F);

        StartEntry startEntry = new StartEntry()
                .addFixedCenter(new Vec3(0, -64, 0))
                .addFixedDimension(new Vec3((double) side / 2, 384, (double) side / 2));
        EndEntry endEntry = new EndEntry()
                .addFixedCenter(new Vec3(0, -64, 0))
                .addFixedDimension(new Vec3((double) endSide / 2, 384, (double) endSide / 2));

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(0, "UHC blue border", "#0000FF77",
                0, 120 * 60 * 20,
                safeFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }
}
