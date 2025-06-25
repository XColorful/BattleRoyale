package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.SafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual.addPhase;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual.generateBorder;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class Pubg881x881Casual {

    private static final String DEFAULT_FILE_NAME = "example_881x881_casual.json";

    private static final int INIT_ZONE_DELAY = 20 * 40; // 40秒
    private static final int ZONE1_MOVE_DELAY = 20 * 100; // 缩圈延迟
    private static final int ZONE1_MOVE_TIME = 20 * 80; // 缩圈时间
    private static final int ZONE1_TIME = ZONE1_MOVE_DELAY + ZONE1_MOVE_TIME;
    private static final double ZONE1_SHRINK_SCALE = Math.sqrt(881*881 * 0.55 / Math.PI) / (440.5 * Math.sqrt(2)); // 缩圈后dimension相对于原先的倍数，约为0.592
    private static final double ZONE1_SHRINK_RANGE = (1 - (4000 * Math.sqrt(2) * ZONE1_SHRINK_SCALE) / 4000) / Math.sqrt(2); // 初始dimension * RANGE = 半径范围，用于确定终点中心
    private static final float ZONE1_DAMAGE = 1F / 5F;

    private static final int ZONE2_MOVE_DELAY = 20 * 100;
    private static final int ZONE2_MOVE_TIME = 20 * 60;
    private static final int ZONE2_TIME = ZONE2_MOVE_DELAY + ZONE2_MOVE_TIME;
    private static final double ZONE2_SHRINK_RANGE = 0.5;
    private static final double ZONE2_SHRINK_SCALE = 0.5;
    private static final float ZONE2_DAMAGE = 3F / 5F;

    private static final int ZONE3_MOVE_DELAY = 20 * 90;
    private static final int ZONE3_MOVE_TIME = 20 * 30;
    private static final int ZONE3_TIME = ZONE3_MOVE_DELAY + ZONE3_MOVE_TIME;
    private static final double ZONE3_SHRINK_RANGE = 0.5;
    private static final double ZONE3_SHRINK_SCALE = 0.5;
    private static final float ZONE3_DAMAGE = 5F / 5F;

    private static final int ZONE4_MOVE_DELAY = 20 * 70;
    private static final int ZONE4_MOVE_TIME = 20 * 30;
    private static final int ZONE4_TIME = ZONE4_MOVE_DELAY + ZONE4_MOVE_TIME;
    private static final double ZONE4_SHRINK_RANGE = 0.5;
    private static final double ZONE4_SHRINK_SCALE = 0.5;
    private static final float ZONE4_DAMAGE = 7F / 5F;

    private static final int ZONE5_MOVE_DELAY = 20 * 60;
    private static final int ZONE5_MOVE_TIME = 20 * 30;
    private static final int ZONE5_TIME = ZONE5_MOVE_DELAY + ZONE5_MOVE_TIME;
    private static final double ZONE5_SHRINK_RANGE = 0.5;
    private static final double ZONE5_SHRINK_SCALE = 0.5;
    private static final float ZONE5_DAMAGE = 9F / 5F;

    private static final int ZONE6_MOVE_DELAY = 20 * 30;
    private static final int ZONE6_MOVE_TIME = 20 * 30;
    private static final int ZONE6_TIME = ZONE6_MOVE_DELAY + ZONE6_MOVE_TIME;
    private static final double ZONE6_SHRINK_RANGE = 0.5;
    private static final double ZONE6_SHRINK_SCALE = 0.001;
    private static final float ZONE6_DAMAGE = 11F / 5F;

    private static final int GAME_TIME = INIT_ZONE_DELAY + ZONE1_TIME + ZONE2_TIME + ZONE3_TIME + ZONE4_TIME + ZONE5_TIME + ZONE6_TIME;

    public static void generateDefaultConfigs() {
        JsonArray zoneConfigJson = new JsonArray();
        add881x881Zone(zoneConfigJson);
        addPhase1(zoneConfigJson);
        addPhase2(zoneConfigJson);
        addPhase3(zoneConfigJson);
        addPhase4(zoneConfigJson);
        addPhase5(zoneConfigJson);
        addPhase6(zoneConfigJson);
        writeJsonToFile(Paths.get(GameConfigManager.get().getZoneConfigPath(), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static void add881x881Zone(JsonArray zoneConfigJson) {
        zoneConfigJson.add(generateBorder(881 / 2F, GAME_TIME));
    }

    private static void addPhase1(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 1, ZONE1_SHRINK_RANGE, ZONE1_SHRINK_SCALE, INIT_ZONE_DELAY, ZONE1_TIME,
                ZONE1_MOVE_DELAY, ZONE1_MOVE_TIME, ZONE1_DAMAGE);
    }

    private static void addPhase2(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 2, ZONE2_SHRINK_RANGE, ZONE2_SHRINK_SCALE, ZONE1_TIME, ZONE2_TIME,
                ZONE2_MOVE_DELAY, ZONE2_MOVE_TIME, ZONE2_DAMAGE);
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
}