package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.SafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.CircleEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual.addPhase;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class Pubg5340x5340Casual {

    private static final String DEFAULT_FILE_NAME = "example_5340x5340_casual.json";

    private static final int INIT_ZONE_DELAY = 20 * 80; // 80秒

    private static final int ZONE2_MOVE_DELAY = 20 * 120;
    private static final int ZONE2_MOVE_TIME = 20 * 90;
    private static final int ZONE2_TIME = ZONE2_MOVE_DELAY + ZONE2_MOVE_TIME;
    private static final double ZONE2_SHRINK_RANGE = 0.45;
    private static final double ZONE2_SHRINK_SCALE = 0.55;
    private static final float ZONE2_DAMAGE = 0.6F / 5F;

    private static final int ZONE3_MOVE_DELAY = 20 * 100;
    private static final int ZONE3_MOVE_TIME = 20 * 80;
    private static final int ZONE3_TIME = ZONE3_MOVE_DELAY + ZONE3_MOVE_TIME;
    private static final double ZONE3_SHRINK_RANGE = 0.4;
    private static final double ZONE3_SHRINK_SCALE = 0.6;
    private static final float ZONE3_DAMAGE = 0.8F / 5F;

    private static final int ZONE4_MOVE_DELAY = 20 * 100;
    private static final int ZONE4_MOVE_TIME = 20 * 80;
    private static final int ZONE4_TIME = ZONE4_MOVE_DELAY + ZONE4_MOVE_TIME;
    private static final double ZONE4_SHRINK_RANGE = 0.45;
    private static final double ZONE4_SHRINK_SCALE = 0.55;
    private static final float ZONE4_DAMAGE = 1F / 5F;

    private static final int ZONE5_MOVE_DELAY = 20 * 100;
    private static final int ZONE5_MOVE_TIME = 20 * 60;
    private static final int ZONE5_TIME = ZONE5_MOVE_DELAY + ZONE5_MOVE_TIME;
    private static final double ZONE5_SHRINK_RANGE = 0.5;
    private static final double ZONE5_SHRINK_SCALE = 0.5;
    private static final float ZONE5_DAMAGE = 3F / 5F;

    private static final int ZONE6_MOVE_DELAY = 20 * 90;
    private static final int ZONE6_MOVE_TIME = 20 * 30;
    private static final int ZONE6_TIME = ZONE6_MOVE_DELAY + ZONE6_MOVE_TIME;
    private static final double ZONE6_SHRINK_RANGE = 0.5;
    private static final double ZONE6_SHRINK_SCALE = 0.5;
    private static final float ZONE6_DAMAGE = 5F / 5F;

    private static final int ZONE7_MOVE_DELAY = 20 * 70;
    private static final int ZONE7_MOVE_TIME = 20 * 30;
    private static final int ZONE7_TIME = ZONE7_MOVE_DELAY + ZONE7_MOVE_TIME;
    private static final double ZONE7_SHRINK_RANGE = 0.5;
    private static final double ZONE7_SHRINK_SCALE = 0.5;
    private static final float ZONE7_DAMAGE = 7F / 5F;

    private static final int ZONE8_MOVE_DELAY = 20 * 60;
    private static final int ZONE8_MOVE_TIME = 20 * 30;
    private static final int ZONE8_TIME = ZONE8_MOVE_DELAY + ZONE8_MOVE_TIME;
    private static final double ZONE8_SHRINK_RANGE = 0.5;
    private static final double ZONE8_SHRINK_SCALE = 0.5;
    private static final float ZONE8_DAMAGE = 9F / 5F;

    private static final int ZONE9_MOVE_DELAY = 20 * 30;
    private static final int ZONE9_MOVE_TIME = 20 * 30;
    private static final int ZONE9_TIME = ZONE9_MOVE_DELAY + ZONE9_MOVE_TIME;
    private static final double ZONE9_SHRINK_RANGE = 0.5;
    private static final double ZONE9_SHRINK_SCALE = 0.001;
    private static final float ZONE9_DAMAGE = 11F / 5F;

    private static final int GAME_TIME = INIT_ZONE_DELAY + ZONE2_TIME + ZONE3_TIME + ZONE4_TIME + ZONE5_TIME + ZONE6_TIME + ZONE7_TIME + ZONE8_TIME + ZONE9_TIME;

    public static void generateDefaultConfigs() {
        JsonArray zoneConfigJson = new JsonArray();
        add5340x5340Zone(zoneConfigJson);
        addPhase2(zoneConfigJson);
        addPhase3(zoneConfigJson);
        addPhase4(zoneConfigJson);
        addPhase5(zoneConfigJson);
        addPhase6(zoneConfigJson);
        addPhase7(zoneConfigJson);
        addPhase8(zoneConfigJson);
        addPhase9(zoneConfigJson);
        writeJsonToFile(Paths.get(GameConfigManager.get().getZoneConfigPath(), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    public static JsonObject generateBorderCircle(float halfWidth, int GAME_TIME) {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, 0, 200, 0, 666); // 固定边界的检查频率低一些

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(0, -64, 0));
        startEntry.addFixedDimension(new Vec3(halfWidth, 384, halfWidth));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addPreviousDimension(0, 0);

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(0, "Blue border", "#0000FFFF",
                -1, 0, GAME_TIME,
                safeFuncEntry, circleEntry);

        return zoneConfig.toJson();
    }

    private static void add5340x5340Zone(JsonArray zoneConfigJson) {
        zoneConfigJson.add(generateBorderCircle(5340 / 2F, GAME_TIME));
    }

    private static void addPhase2(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 2, ZONE2_SHRINK_RANGE, ZONE2_SHRINK_SCALE, INIT_ZONE_DELAY, ZONE2_TIME,
                ZONE2_MOVE_DELAY, ZONE2_MOVE_TIME, ZONE2_DAMAGE, 2);
    }

    private static void addPhase3(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 3, ZONE3_SHRINK_RANGE, ZONE3_SHRINK_SCALE, ZONE2_TIME, ZONE3_TIME,
                ZONE3_MOVE_DELAY, ZONE3_MOVE_TIME, ZONE3_DAMAGE);
    }

    private static void addPhase4(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 4, ZONE4_SHRINK_RANGE, ZONE4_SHRINK_SCALE, ZONE3_TIME, ZONE4_TIME,
                ZONE4_MOVE_DELAY, ZONE4_MOVE_TIME, ZONE4_DAMAGE);
    }

    private static void addPhase5(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 5, ZONE5_SHRINK_RANGE, ZONE5_SHRINK_SCALE, ZONE4_TIME, ZONE5_TIME,
                ZONE5_MOVE_DELAY, ZONE5_MOVE_TIME, ZONE5_DAMAGE);
    }

    private static void addPhase6(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 6, ZONE6_SHRINK_RANGE, ZONE6_SHRINK_SCALE, ZONE5_TIME, ZONE6_TIME,
                ZONE6_MOVE_DELAY, ZONE6_MOVE_TIME, ZONE6_DAMAGE);
    }

    private static void addPhase7(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 7, ZONE7_SHRINK_RANGE, ZONE7_SHRINK_SCALE, ZONE6_TIME, ZONE7_TIME,
                ZONE7_MOVE_DELAY, ZONE7_MOVE_TIME, ZONE7_DAMAGE);
    }

    private static void addPhase8(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 8, ZONE8_SHRINK_RANGE, ZONE8_SHRINK_SCALE, ZONE7_TIME, ZONE8_TIME,
                ZONE8_MOVE_DELAY, ZONE8_MOVE_TIME, ZONE8_DAMAGE);
    }

    private static void addPhase9(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 9, ZONE9_SHRINK_RANGE, ZONE9_SHRINK_SCALE, ZONE8_TIME, ZONE9_TIME,
                ZONE9_MOVE_DELAY, ZONE9_MOVE_TIME, ZONE9_DAMAGE);
    }
}