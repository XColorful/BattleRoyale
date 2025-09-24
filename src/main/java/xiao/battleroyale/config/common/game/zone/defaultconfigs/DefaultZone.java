package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.*;
import xiao.battleroyale.config.common.game.zone.zonefunc.EffectFuncEntry.EffectFuncEntryBuilder;
import xiao.battleroyale.config.common.game.zone.zonefunc.event.EventFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

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
        zoneConfigJson.add(generateDefaultZoneConfig6());
        zoneConfigJson.add(generateDefaultZoneConfig7());
        zoneConfigJson.add(generateDefaultZoneConfig10());
        zoneConfigJson.add(generateDefaultZoneConfig11());
        zoneConfigJson.add(generateDefaultZoneConfig12());
        zoneConfigJson.add(generateDefaultZoneConfig13());
        zoneConfigJson.add(generateDefaultZoneConfig14());
        zoneConfigJson.add(generateDefaultZoneConfig15());
        writeJsonToFile(Paths.get(GameConfigManager.get().getZoneConfigPath(), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static JsonObject generateDefaultZoneConfig0() {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, 0, 20, 0, 1.0F); // 直接结束缩圈

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(0, -60, 0));
        startEntry.addFixedDimension(new Vec3(128, 255, 128));
        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(0, -60, 0));
        endEntry.addFixedDimension(new Vec3(128, 255, 128));

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(0, "Blue opaque border", "#0000FFFF",
                0, 12000,
                safeFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig1() {
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(600, 1200, 20, -1, 1.0F); // 30秒后刷圈，缩圈1分钟

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(0, 1);
        startEntry.addPreviousDimension(0, 1);
        startEntry.addDimensionScale(Math.sqrt(2));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(1, 0);
        endEntry.addCenterRange(64);
        endEntry.addPreviousDimension(1, 0);
        endEntry.addDimensionScale(0.5);

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(1, "Self shrink aqua circle", "#00FFFFAA",
                0, 12000,
                safeFuncEntry, circleEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig2() {
        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(600, 1200, 20, -1, 1.0F); // 30秒后刷圈，缩圈1分钟

        // 中心点(0, 0)，初始圆包含 Simple Border 的边界正方形，缩圈后半径为原先 0.5 倍
        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(0, -30, 0));
        startEntry.addFixedDimension(new Vec3(30, 20, 50));
        startEntry.addCenterRange(128);
        startEntry.addDimensionRange(10);

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(0, -50, 0));
        endEntry.addCenterRange(64);
        endEntry.addFixedDimension(new Vec3(30, 20, 50));
        endEntry.addDimensionRange(20);

        RectangleEntry rectangleEntry = new RectangleEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(2, "Floating unsafe red rectangle", "#FF0000AA",
                200, 2400,
                unsafeFuncEntry, rectangleEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig3() {
        FireworkFuncEntry fireworkFuncEntry = new FireworkFuncEntry(600, 1200, 20, -1, // 30秒后刷圈，缩圈1分钟
                true, 1, 20, 5, 3, false);

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(1, 1);
        startEntry.addRelativeDimension(new Vec3(0, 10, 0));
        startEntry.addPreviousDimension(1, 1);
        startEntry.addRelativeDimension(new Vec3(-50, -230, -50));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(3, 0);
        endEntry.addRelativeCenter(new Vec3(0, 20, 0));
        endEntry.addPreviousDimension(3, 0);
        endEntry.addRelativeDimension(new Vec3(50, -5, 50));

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(3, "Relative Green Circle", "#00FF00AA",
                400, 11600,
                fireworkFuncEntry, circleEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig4() {
        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(600, 600, 20, -1, 10.0F);

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(0, 1);
        startEntry.addRelativeCenter(new Vec3(0, 0, 0));
        startEntry.addPreviousDimension(0, 1);
        startEntry.addRelativeDimension(new Vec3(-100, -240, -100));
        startEntry.addDimensionRange(10);
        startEntry.addPlayerCenterLerp(0.2);

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(4, 0);
        endEntry.addRelativeCenter(new Vec3(0, -10, 0));
        endEntry.addCenterRange(100);
        endEntry.addPreviousCenter(4, 0);
        endEntry.addRelativeCenter(new Vec3(0, 10, 0));

        HexagonEntry hexagonEntry = new HexagonEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(4, "Unwelcomed White Hexagon", "#FFFFFFAA",
                600, 11400,
                unsafeFuncEntry, hexagonEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig5() {
        UnsafeFuncEntry unsafeFuncEntry = new UnsafeFuncEntry(600, 600, 20, -1, 0.001F);

        StartEntry startEntry = new StartEntry();
        startEntry.addLockCenter(0, true);
        startEntry.addFixedDimension(new Vec3(80, 2, 80));

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(0, -60, 0));
        endEntry.addFixedDimension(new Vec3(15, 4, 15));

        PolygonEntry polygonEntry = new PolygonEntry(startEntry, endEntry, false, 5);

        ZoneConfig zoneConfig = new ZoneConfig(5, "Black Polygon Trap", "#00000022",
                100, 12000,
                unsafeFuncEntry, polygonEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig6() {
        FireworkFuncEntry fireworkFuncEntry = new FireworkFuncEntry(0, 400, 10, -1,
                false, 1, 0, 0, 0, false);

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(0, 0);
        startEntry.addFixedDimension(new Vec3(128, 1.5, 12.8));
        startEntry.addFixedRotate(-360);

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addPreviousDimension(6, 0);
        endEntry.addPreviousRotate(6, 0);
        endEntry.addRelativeRotate(360 * 2);

        EllipseEntry ellipseEntry = new EllipseEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(6, "Rotating Ellipse monitor", "#FFFF0022",
                200, 400,
                fireworkFuncEntry, ellipseEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig7() {
        MutekiFuncEntry mutekiFuncEntry = new MutekiFuncEntry(200, 400, 20, -1,
                200);

        StartEntry startEntry = new StartEntry();
        startEntry.addLockCenter(0, true);
        startEntry.addCenterRange(10);
        startEntry.addFixedDimension(new Vec3(20, 5, 10));
        startEntry.addFixedRotate(-360);

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(7, 0);
        endEntry.addPreviousDimension(7, 0);
        endEntry.addRelativeDimension(new Vec3(-40, -2, 0));
        endEntry.addPlayerCenterLerp(1);
        endEntry.addPreviousRotate(7, 0);
        endEntry.addRelativeRotate(720);

        StarEntry starEntry = new StarEntry(startEntry, endEntry, true, 5);

        ZoneConfig zoneConfig = new ZoneConfig(7, "Bug Star", "#FFD70022",
                200, 700,
                mutekiFuncEntry, starEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig10() {
        BoostFuncEntry boostFuncEntry = new BoostFuncEntry(200, 400, 20, -1, 80);

        StartEntry startEntry = new StartEntry();
        startEntry.addLockCenter(1, true);
        startEntry.addFixedDimension(new Vec3(10, 10, 10));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addRelativeCenter(new Vec3(0, 5, 0));
        endEntry.addPreviousDimension(10, 0);

        SphereEntry sphereEntry = new SphereEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(10, "1st boost sphere", "#0000FF77",
                200, 700,
                boostFuncEntry, sphereEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig11() {
        ParticleFuncEntry particleFuncEntry = new ParticleFuncEntry(200, 400, 20, -1,
                Arrays.asList(1, 1, 2), 1,"zone11", 50);

        StartEntry startEntry = new StartEntry();
        startEntry.addLockCenter(0, true);
        startEntry.addPlayerCenterLerp(-0.2);
        startEntry.addFixedDimension(new Vec3(10, 10, 10));
        startEntry.addLockRotate(0);

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addRelativeCenter(new Vec3(0, 9.99, 0));
        endEntry.addCenterRange(20);
        endEntry.addPreviousDimension(11, 0);
        endEntry.addPreviousRotate(11, 0);
        endEntry.addRotateRange(360);

        CubeEntry cubeEntry = new CubeEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(11, "Particle Cube", "#0000FF55",
                200, 700,
                particleFuncEntry, cubeEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig12() {
        EffectFuncEntry effectFuncEntry = new EffectFuncEntryBuilder(200, 400, 20, -1)
                .add("minecraft:speed", 20, 1)
                .add("minecraft:jump_boost", 20, 1)
                .build();

        StartEntry startEntry = new StartEntry();
        startEntry.addLockCenter(0, true);
        startEntry.addPlayerCenterLerp(0.2);
        startEntry.addFixedDimension(new Vec3(20, 10, 10));
        startEntry.addLockRotate(0);

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addRelativeCenter(new Vec3(0, 9.99, 0));
        endEntry.addCenterRange(20);
        endEntry.addPreviousDimension(11, 0);
        endEntry.addPreviousRotate(11, 0);
        endEntry.addRotateRange(360);

        CuboidEntry cuboidEntry = new CuboidEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(12, "Speed effect Cuboid", "#00FF0099",
                200, 700,
                effectFuncEntry, cuboidEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig13() {
        NoFuncEntry noFuncEntry = new NoFuncEntry(0, 1200);

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(0, 0);
        startEntry.addRelativeCenter(new Vec3(0, 50, 0));
        startEntry.addFixedDimension(new Vec3(15, 5, 8));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(13, 0);
        endEntry.addPreviousDimension(13, 0);
        endEntry.addFixedRotate(360 * 15);

        EllipsoidEntry ellipsoidEntry = new EllipsoidEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(13, "Harmonious ellipsoid spectator", "#FF000066",
                600, 1800,
                noFuncEntry, ellipsoidEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig14() {
        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 40, 25, 10,
                true, 10, 80, 20,
                true, Component.literal("§6Game Start").withStyle(ChatFormatting.BOLD),
                true, Component.literal(""),
                true, Component.literal("Zone 0").withStyle(ChatFormatting.BLUE));

        StartEntry startEntry = new StartEntry();
        startEntry.addPreviousCenter(0, 0);
        startEntry.addFixedDimension(new Vec3(0, 255, 0));
        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addPreviousDimension(0, 1);
        endEntry.addDimensionScale(0.99);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(14, "Triple zone notification", "#FFAA00AA",
                0, 80,
                messageFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig15() {
        CompoundTag tag = new CompoundTag();
        tag.putString("description", "Create event for other mod to subscribe");
        tag.putBoolean("boolTrue", true);
        tag.putBoolean("boolFalse", false);
        tag.putFloat("float", 0.333F);
        tag.putDouble("double", 0.88888888D);
        tag.putLong("long", Integer.MAX_VALUE * 2L);
        tag.putInt("int", 666666);
        tag.putShort("short", (short) 25565);
        tag.putString("randomUUID", UUID.randomUUID().toString());
        CompoundTag nestedTag = new CompoundTag();
        nestedTag.putString("additional data", "some structured data");
        tag.put("tagInTag", nestedTag);

        EventFuncEntry eventFuncEntry = new EventFuncEntry(0, 0, 0, 0,
                "cbr:0.3.8", tag);

        StartEntry startEntry = new StartEntry();
        EndEntry endEntry = new EndEntry();
        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(15, "Event Zone example", "#FFFFFFFF",
                0, 0,
                eventFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }
}