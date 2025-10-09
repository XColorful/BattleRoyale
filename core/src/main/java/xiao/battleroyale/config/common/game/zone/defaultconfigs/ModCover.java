package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.NoFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.CircleEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class ModCover {

    private static final String DEFAULT_FILE_NAME = "example_mod_cover";

    public static void generateDefaultConfigs() {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateBorder());
        zoneConfigJson.add(generateInner());
        writeJsonToFile(Paths.get(String.valueOf(ZoneConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static JsonObject generateBorder() {
        NoFuncEntry noFuncEntry = new NoFuncEntry(0, 0);

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(-270,0,150));
        startEntry.addFixedDimension(new Vec3(260, 120, 260));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(0, 0);
        endEntry.addPreviousDimension(0, 0);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(0, "Boarder", "#0000FFAA", 0, 120000,
                noFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateInner() {
        NoFuncEntry noFuncEntry = new NoFuncEntry(0, 0);

        StartEntry startEntry = new StartEntry();
        startEntry.addFixedCenter(new Vec3(-298, 0, 116));
        startEntry.addFixedDimension(new Vec3(211, 80, 211));

        EndEntry endEntry = new EndEntry();
        endEntry.addPreviousCenter(1, 0);
        endEntry.addPreviousDimension(1, 0);

        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);

        ZoneConfig zoneConfig = new ZoneConfig(1, "Circle", "#00FFFF88", 0, 120000,
                noFuncEntry, circleEntry);

        return zoneConfig.toJson();
    }
}