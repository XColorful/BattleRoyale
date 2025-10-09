package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.*;
import xiao.battleroyale.config.common.game.zone.zoneshape.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class FunctionShowcase {

    private static final String DEFAULT_FILE_NAME = "example_function_showcase.json";

    public static void generateDefaultConfigs() {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateDefaultZoneConfig0());
        zoneConfigJson.add(generateDefaultZoneConfig1());
        zoneConfigJson.add(generateDefaultZoneConfig2());
        zoneConfigJson.add(generateDefaultZoneConfig3());
        zoneConfigJson.add(generateDefaultZoneConfig4());
        zoneConfigJson.add(generateDefaultZoneConfig5());
        writeJsonToFile(Paths.get(String.valueOf(ZoneConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static JsonObject generateDefaultZoneConfig0() {
        EffectFuncEntry effectFuncEntry = new EffectFuncEntry(0, 10, 0, 0, Arrays.asList(
                new EffectFuncEntry.EffectEntry("minecraft:speed", 20, 3)
        ));

        StartEntry startEntry = new StartEntry();
        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(2022, 53, -9059));
        endEntry.addFixedDimension(new Vec3(2, 2, 15));

        RectangleEntry rectangleEntry = new RectangleEntry(startEntry, endEntry, false);
        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(0, "First speed rectangle", "#00FFFF33",
                0, 99999,
                effectFuncEntry, rectangleEntry);
        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig1() {
        NoFuncEntry noFuncEntry = new NoFuncEntry(0, 20 * 10);

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(2013, 57, -9043));
        startEntry.addFixedDimension(new Vec3(6, 4, 2));

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(2013, 57, -9043));
        endEntry.addFixedDimension(new Vec3(6, 4, 2));
        endEntry.addFixedRotate(360 * 10);

        EllipsoidEntry ellipsoidEntry = new EllipsoidEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(1, "Rotating ellipsoid", "#FF000033",
                0, 99999,
                noFuncEntry, ellipsoidEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig2() {
        FireworkFuncEntry fireworkFuncEntry = new FireworkFuncEntry(0, 10, 20, 0,
                true, 1, 20, 1, 1, false);

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(2011, 53, -9052));
        startEntry.addFixedDimension(new Vec3(4, 1, 4));

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(2011, 53, -9052));
        endEntry.addFixedDimension(new Vec3(4, 1, 4));

        HexagonEntry hexagonEntry = new HexagonEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(2, "Firework hexagon", "#00FF0033",
                0, 99999,
                fireworkFuncEntry, hexagonEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig3() {
        ParticleFuncEntry particleFuncEntry = new ParticleFuncEntry(0, 10, 1, 0,
                Arrays.asList(0, 1, 2), 1, "zone3", 5);

        StartEntry startEntry = new StartEntry();

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(1999, 53.5 + 2, -9045));
        endEntry.addFixedDimension(new Vec3(2, 2, 2));

        CubeEntry cubeEntry = new CubeEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(3, "Floating cube particle", "#0000FF33",
                0, 99999,
                particleFuncEntry, cubeEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig4() {
        BoostFuncEntry boostFuncEntry = new BoostFuncEntry(0, 10, 1, 0, 100);

        StartEntry startEntry = new StartEntry();

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(1982, 53, -9047));
        endEntry.addFixedDimension(new Vec3(5, 5, 5));

        SphereEntry sphereEntry = new SphereEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(4, "Boost sphere", "#FFFFFF33",
                0, 99999,
                boostFuncEntry, sphereEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateDefaultZoneConfig5() {
        MutekiFuncEntry mutekiFuncEntry = new MutekiFuncEntry(200, 100, 1, 0, 20);

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(1970, 53, -9040));
        startEntry.addFixedDimension(new Vec3(6, 1, 3));

        EndEntry endEntry = new EndEntry();
        endEntry.addFixedCenter(new Vec3(1970, 53, -9040));
        endEntry.addFixedDimension(new Vec3(2, 0.5, 6));

        StarEntry starEntry = new StarEntry(startEntry, endEntry, true, 5);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(5, "Muteki Star", "#FFD700AA",
                0, 99999,
                mutekiFuncEntry, starEntry);

        return zoneConfig.toJson();
    }
}