package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.zone.ZoneConfigTag;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.SafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zonefunc.UnsafeFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.*;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultZone {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateDefaultZoneConfig0());
        zoneConfigJson.add(generateDefaultZoneConfig1());
        zoneConfigJson.add(generateDefaultZoneConfig2());
        zoneConfigJson.add(generateDefaultZoneConfig3());
        zoneConfigJson.add(generateDefaultZoneConfig4());
        zoneConfigJson.add(generateDefaultZoneConfig5());
        writeJsonToFile(Paths.get(GameConfigManager.get().getZoneConfigPath(), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static JsonObject generateDefaultZoneConfig0() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 0);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Blue opaque border");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#0000FFFF");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 0);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 12000);

        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(1, 0, 0); // 直接结束缩圈

        config.add(ZoneConfigTag.ZONE_FUNC, safeFuncEntry.toJson());

        StartEntry startEntry = new StartEntry(StartCenterType.FIXED, new Vec3(0, -60, 0), -1, 0,
                StartDimensionType.FIXED, new Vec3(128, 255, 128), -1, 1, 0);
        EndEntry endEntry = new EndEntry(EndCenterType.FIXED, new Vec3(0, -60, 0), -1, 0,
                EndDimensionType.FIXED, new Vec3(128, 255, 128), -1, 1, 0);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry);
        config.add(ZoneConfigTag.ZONE_SHAPE, squareEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultZoneConfig1() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 1);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Self shrink aqua circle");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#00FFFFAA");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 0);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 12000);

        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(1, 600, 1200); // 30秒后刷圈，缩圈1分钟

        config.add(ZoneConfigTag.ZONE_FUNC, safeFuncEntry.toJson());

        StartEntry startEntry = new StartEntry(StartCenterType.PREVIOUS, new Vec3(-1, -1, -1), 0, 0,
                StartDimensionType.PREVIOUS, new Vec3(-1, -1, -1), 0, Math.sqrt(2), 0);
        EndEntry endEntry = new EndEntry(EndCenterType.PREVIOUS, new Vec3(0, 0, 0), 1, 64,
                EndDimensionType.PREVIOUS, new Vec3(-1, -1, -1), 1, 0.5, 0);

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry);
        config.add(ZoneConfigTag.ZONE_SHAPE, circleEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultZoneConfig2() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 2);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Floating unsafe red rectangle");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#FF0000AA");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 200);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 2400);

        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(1, 600, 1200); // 30秒后刷圈，缩圈1分钟

        config.add(ZoneConfigTag.ZONE_FUNC, unsafeFuncEntry.toJson());

        // 中心点(0, 0)，初始圆包含 Simple Border 的边界正方形，缩圈后半径为原先 0.5 倍
        StartEntry startEntry = new StartEntry(StartCenterType.FIXED, new Vec3(0, -30, 0), -1, 128,
                StartDimensionType.FIXED, new Vec3(30, 20, 50), -1, -1, 10);
        EndEntry endEntry = new EndEntry(EndCenterType.FIXED, new Vec3(0, -50, 0), -1, 64,
                EndDimensionType.FIXED, new Vec3(30, 20, 50), -1, -1, 20);

        RectangleEntry rectangleEntry = new RectangleEntry(startEntry, endEntry);
        config.add(ZoneConfigTag.ZONE_SHAPE, rectangleEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultZoneConfig3() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 3);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Relative Green Circle");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#00FF00AA");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 400);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 11600);

        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(2, 600, 1200); // 30秒后刷圈，缩圈1分钟

        config.add(ZoneConfigTag.ZONE_FUNC, unsafeFuncEntry.toJson());

        StartEntry startEntry = new StartEntry(StartCenterType.RELATIVE, new Vec3(0, 10, 0), 1, 0,
                StartDimensionType.RELATIVE, new Vec3(-50, -230, -50), 1, 1, 0);
        EndEntry endEntry = new EndEntry(EndCenterType.RELATIVE, new Vec3(0, 20, 0), 3, 0,
                EndDimensionType.RELATIVE, new Vec3(50, -5, 50), 3, 1, 0);

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry);
        config.add(ZoneConfigTag.ZONE_SHAPE, circleEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultZoneConfig4() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 4);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Relative White Hexagon");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#FFFFFFAA");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 600);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 11400);

        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(10, 600, 600);

        config.add(ZoneConfigTag.ZONE_FUNC, unsafeFuncEntry.toJson());

        StartEntry startEntry = new StartEntry(StartCenterType.RELATIVE, new Vec3(0, 0, 0), 0, 0,
                StartDimensionType.RELATIVE, new Vec3(-100, -240, -100), 0, 1, 10);
        EndEntry endEntry = new EndEntry(EndCenterType.RELATIVE, new Vec3(0, -10, 0), 4, 100,
                EndDimensionType.RELATIVE, new Vec3(0, 10, 0), 4, 1, 0);

        HexagonEntry hexagonEntry = new HexagonEntry(startEntry, endEntry);
        config.add(ZoneConfigTag.ZONE_SHAPE, hexagonEntry.toJson());

        return config;
    }

    private static JsonObject generateDefaultZoneConfig5() {
        JsonObject config = new JsonObject();
        config.addProperty(ZoneConfigTag.ZONE_ID, 5);
        config.addProperty(ZoneConfigTag.ZONE_NAME, "Black Polygon");
        config.addProperty(ZoneConfigTag.ZONE_COLOR, "#00000022");
        config.addProperty(ZoneConfigTag.ZONE_DELAY, 0);
        config.addProperty(ZoneConfigTag.ZONE_TIME, 12000);

        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(0.001, 600, 600);

        config.add(ZoneConfigTag.ZONE_FUNC, unsafeFuncEntry.toJson());

        StartEntry startEntry = new StartEntry(StartCenterType.FIXED, new Vec3(0, -60, 0), -1, 0,
                StartDimensionType.FIXED, new Vec3(80, 2, 80), -1, 1, 0);
        EndEntry endEntry = new EndEntry(EndCenterType.FIXED, new Vec3(0, -60, 0), -1, 0,
                EndDimensionType.FIXED, new Vec3(15, 4, 15), -1, 1, 0);

        PolygonEntry polygonEntry = new PolygonEntry(startEntry, endEntry, 5);
        config.add(ZoneConfigTag.ZONE_SHAPE, polygonEntry.toJson());

        return config;
    }
}