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

public class UHC1000x1000Complex {

    private static final String DEFAULT_FILE_NAME = "example_UHC_1000x1000_50minutes_complex.json";

    // 共50分钟
    // 每5分钟为1阶段, 每阶段多扣0.5血
    private static final int _50_minutes = 50 * 60 * 20;
    private static final int _5_minutes = 5 * 60 * 20;
    private static final int MAX_GAME_TIME = 60 * 60 * 20; // 上限1小时

    private static final double INITIAL_BORDER_SIDE = 1000;

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateFirstBorder(INITIAL_BORDER_SIDE, MAX_GAME_TIME));
        for (int i = 1; i <= 10; i++) {
            zoneConfigJson.add(generateBorder(i, _5_minutes, _50_minutes, INITIAL_BORDER_SIDE));
        }
        zoneConfigJson.add(generateFinalBorder(11, _5_minutes, _50_minutes, MAX_GAME_TIME));
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    public static JsonObject generateFirstBorder(double side, int maxGameTime) {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, 0, 20 * 10, 0, 666);

        StartEntry startEntry = new StartEntry()
                .addFixedCenter(new Vec3(0, -64, 0))
                .addFixedDimension(new Vec3(side / 2, 384, side / 2));
        EndEntry endEntry = new EndEntry()
                .addFixedCenter(new Vec3(0, -64, 0))
                .addFixedDimension(new Vec3(side / 2, 384, side / 2));

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(0, "UHC border", "#FFFFFF77",
                0, maxGameTime,
                safeFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    public static JsonObject generateBorder(int phase, int timePerPhase, int timeAllPhase, double initBorderSide) {
        int prePhase = phase - 1;
        int zoneId = phase * 10;
        int preZoneId = prePhase * 10;
        int totalPhaseCount = timeAllPhase / timePerPhase;
        double shrinkBorderSide = initBorderSide / totalPhaseCount;

        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, timePerPhase, 20, 0, phase * 0.5F);

        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(preZoneId, 1)
                .addPreviousDimension(preZoneId, 1);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(zoneId, 0)
                .addRelativeDimension(new Vec3(-shrinkBorderSide / 2, 0, -shrinkBorderSide / 2));

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        String zoneName = String.format("Phase %s border", phase);
        int zoneColorInt = (int) (0xFF * phase / (float) totalPhaseCount);
        String zoneColor = "#0000FF" + String.format("%02X", zoneColorInt);; // #RRGGBBAA
        ZoneConfig zoneConfig = new ZoneConfig(zoneId, zoneName, zoneColor, preZoneId,
                timePerPhase, timePerPhase,
                safeFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    public static JsonObject generateFinalBorder(int phase, int timePerPhase, int timeAllPhase, int eternalTime) {
        int prePhase = phase - 1;
        int zoneId = phase * 10;
        int preZoneId = prePhase * 10;
        int totalPhaseCount = timeAllPhase / timePerPhase;

        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, 0, 20, 0, phase * 0.5F);

        StartEntry startEntry = new StartEntry().addPreviousCenter(preZoneId, 1).addPreviousDimension(preZoneId, 1);
        EndEntry endEntry = new EndEntry().addPreviousCenter(zoneId, 0).addPreviousDimension(zoneId, 0);
        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        String zoneName = String.format("Phase %s border", phase);
        int zoneColorInt = (int) (0xFF * phase / (float) totalPhaseCount);
        String zoneColor = "#0000FF" + String.format("%02X", zoneColorInt);; // #RRGGBBAA
        ZoneConfig zoneConfig = new ZoneConfig(zoneId, zoneName, zoneColor, preZoneId,
                timePerPhase, eternalTime,
                safeFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }
}
