package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.MessageFuncEntry;
import xiao.battleroyale.config.common.game.zone.zonefunc.NoFuncEntry;
import xiao.battleroyale.config.common.game.zone.zonefunc.SafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.CircleEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class Pubg8000x8000Casual {

    private static final String DEFAULT_FILE_NAME = "example_8000x8000_casual.json";

    private static final int INIT_ZONE_DELAY = 20 * 90; // 90秒
    private static final int ZONE1_MOVE_DELAY = 20 * 240; // 缩圈延迟
    private static final int ZONE1_MOVE_TIME = 20 * 270; // 缩圈时间
    private static final int ZONE1_TIME = ZONE1_MOVE_DELAY + ZONE1_MOVE_TIME;
    private static final double ZONE1_SHRINK_SCALE = Math.sqrt(8000*8000 * 0.35 / Math.PI) / (4000 * Math.sqrt(2)); // 缩圈后dimension相对于原先的倍数，约为0.472
    private static final double ZONE1_SHRINK_RANGE = (1 - (4000 * Math.sqrt(2) * ZONE1_SHRINK_SCALE) / 4000) / Math.sqrt(2); // 初始dimension * RANGE = 半径范围，用于确定终点中心
    private static final float ZONE1_DAMAGE = 0.4F / 5F;

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

    public static final int GAME_TIME = INIT_ZONE_DELAY + ZONE1_TIME + ZONE2_TIME + ZONE3_TIME + ZONE4_TIME + ZONE5_TIME + ZONE6_TIME + ZONE7_TIME + ZONE8_TIME + ZONE9_TIME;

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        add8000x8000Zone(zoneConfigJson);
        addPhase1(zoneConfigJson);
        addPhase2(zoneConfigJson);
        addPhase3(zoneConfigJson);
        addPhase4(zoneConfigJson);
        addPhase5(zoneConfigJson);
        addPhase6(zoneConfigJson);
        addPhase7(zoneConfigJson);
        addPhase8(zoneConfigJson);
        addPhase9(zoneConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    // 游戏边界
    public static void generateBorder(JsonArray zoneConfigJson, float halfWidth, int GAME_TIME) {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, 0, 200, 0, 666); // 固定边界的检查频率低一些

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(0, -64, 0));
        startEntry.addFixedDimension(new Vec3(halfWidth, 384, halfWidth));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addPreviousDimension(0, 0);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(0, "Blue border", "#0000FFFF",
                -1, 0, GAME_TIME,
                safeFuncEntry, squareEntry);

        zoneConfigJson.add(zoneConfig.toJson());

        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 0, 25, 10,
                true, 10, 80, 20,
                true, Component.literal("§6Game Start").withStyle(ChatFormatting.BOLD), Component.literal(""),
                false, Component.literal(""));
        startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(0.99);
        endEntry = new EndEntry().addPreviousCenter(0, 1)
                .addPreviousDimension(0, 1)
                .addDimensionScale(0.99);
        squareEntry = new SquareEntry(startEntry, endEntry, false);
        zoneConfig = new ZoneConfig(1, "Game Start Message", "#FFAA00AA",
                0, 80,
                messageFuncEntry, squareEntry);
        zoneConfigJson.add(zoneConfig.toJson());
    }

    private static void add8000x8000Zone(JsonArray zoneConfigJson) {
        generateBorder(zoneConfigJson, 8000 / 2F, GAME_TIME);
    }

    public static void addPhase(JsonArray zoneConfigJson, int phase, double SHRINK_RANGE, double SHRINK_SCALE, int PRE_ZONE_TIME, int ZONE_TIME,
                                int MOVE_DELAY, int MOVE_TIME, float DAMAGE) {
        addPhase(zoneConfigJson, phase, SHRINK_RANGE, SHRINK_SCALE, PRE_ZONE_TIME, ZONE_TIME,
                MOVE_DELAY, MOVE_TIME, DAMAGE, 1);
    }

    // 当phase为1时，自动将边长乘以根号2倍
    public static void addPhase(JsonArray zoneConfigJson, int phase, double SHRINK_RANGE, double SHRINK_SCALE, int PRE_ZONE_TIME, int ZONE_TIME,
                                int MOVE_DELAY, int MOVE_TIME, float DAMAGE, int prePhaseMinus) {
        // Forecast zone
        int forecastPhase = phase * 10;
        NoFuncEntry noFuncEntry = new NoFuncEntry(0, 20);
        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(forecastPhase - prePhaseMinus * 10, 1)
                .addPreviousDimension(forecastPhase - prePhaseMinus * 10, 1)
                .addDimensionScale(phase == 1 ? Math.sqrt(2) : 1); // 边界正方形半边长 * sqrt(2) = 边界正方形外接圆半径
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(forecastPhase, 0)
                .addCenterRange(SHRINK_RANGE, true, phase > 1) // 扩散，第一个圈用方形随机
                .addPreviousDimension(forecastPhase, 0)
                .addDimensionScale(SHRINK_SCALE); // 缩小
        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);
        ZoneConfig zoneConfig = new ZoneConfig(forecastPhase, "Phase" + phase + "Forecast", "#00FF0033",
                forecastPhase - prePhaseMinus * 10, PRE_ZONE_TIME, ZONE_TIME,
                noFuncEntry, circleEntry);
        zoneConfigJson.add(zoneConfig.toJson());

        // Shrink zone
        int shrinkPhase = forecastPhase + 1;
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(MOVE_DELAY, MOVE_TIME, 20, -1, DAMAGE);
        startEntry = new StartEntry()
                .addPreviousCenter(forecastPhase, 0)
                .addPreviousDimension(forecastPhase, 0);
        endEntry = new EndEntry()
                .addPreviousCenter(forecastPhase, 1)
                .addPreviousCenter(forecastPhase, 1)
                .addPreviousDimension(forecastPhase, 1);
        circleEntry = new CircleEntry(startEntry, endEntry, false);
        zoneConfig = new ZoneConfig(shrinkPhase, "Phase" + phase + "Shrink", "#0000FFAA",
                forecastPhase, 0, ZONE_TIME,
                safeFuncEntry, circleEntry);
        zoneConfigJson.add(zoneConfig.toJson());

        // Zone create message
        int createPhase = shrinkPhase + 1;
        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 0, 25, 10,
                true, 10, 80, 20,
                true, Component.literal(""), Component.literal(String.format("§9Phase§b %s", phase)),
                false, Component.literal(""));
        startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(0.99);
        endEntry = new EndEntry()
                .addPreviousCenter(0, 1)
                .addPreviousDimension(0, 1)
                .addDimensionScale(0.99);
        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);
        zoneConfig = new ZoneConfig(createPhase, String.format("Phase %s create message", phase), "#5555FFFF",
                forecastPhase, 0, 80,
                messageFuncEntry, squareEntry);
        zoneConfigJson.add(zoneConfig.toJson());

        // Zone shrink message
        int warnPhase = createPhase + 1;
        messageFuncEntry = new MessageFuncEntry(0, 0, 25, 10,
                true, 10, 80, 20,
                true, Component.literal(""), Component.literal(String.format("§9Phase§b %s §cShrinking", phase)),
                false, Component.literal(""));
        startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(0.99);
        endEntry = new EndEntry()
                .addPreviousCenter(0, 1)
                .addPreviousDimension(0, 1)
                .addDimensionScale(0.99);
        squareEntry = new SquareEntry(startEntry, endEntry, false);
        zoneConfig = new ZoneConfig(warnPhase, String.format("Phase %s shrink message", phase), "#FF5555FF",
                forecastPhase, MOVE_DELAY, 80,
                messageFuncEntry, squareEntry);
        zoneConfigJson.add(zoneConfig.toJson());
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
