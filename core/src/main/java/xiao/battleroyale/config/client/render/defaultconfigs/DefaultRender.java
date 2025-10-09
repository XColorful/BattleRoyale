package xiao.battleroyale.config.client.render.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.client.render.RenderConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager.RenderConfig;
import xiao.battleroyale.config.client.render.type.BlockEntry;
import xiao.battleroyale.config.client.render.type.SpectateEntry;
import xiao.battleroyale.config.client.render.type.TeamEntry;
import xiao.battleroyale.config.client.render.type.ZoneEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultRender {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray renderConfigJson = new JsonArray();
        renderConfigJson.add(generateDefaultRenderConfig0());
        renderConfigJson.add(generateDefaultRenderConfig1());
        renderConfigJson.add(generateDefaultRenderConfig2());
        renderConfigJson.add(generateDefaultRenderConfig3());
        writeJsonToFile(Paths.get(String.valueOf(RenderConfigManager.get().getConfigDirPath()), DEFAULT_FILE_NAME).toString(), renderConfigJson);
    }

    private static JsonObject generateDefaultRenderConfig0() {
        BlockEntry blockEntry = new BlockEntry(16, true, 16);
        ZoneEntry zoneEntry = new ZoneEntry(false);
        TeamEntry teamEntry = new TeamEntry(true, false);
        SpectateEntry spectateEntry = new SpectateEntry(true, false);

        RenderConfig renderConfig = new RenderConfig(0, "No limit", "#FFFFFFAA", true,
                blockEntry, zoneEntry, teamEntry, spectateEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig1() {
        BlockEntry blockEntry = new BlockEntry(0, false, 0);
        ZoneEntry zoneEntry = new ZoneEntry(true, "#0000FF", 64, 64, 64, 64);
        TeamEntry teamEntry = new TeamEntry(true, true);
        SpectateEntry spectateEntry = new SpectateEntry(true, true);

        RenderConfig renderConfig = new RenderConfig(1, "Client single color", "#FFFFFFAA",
                blockEntry, zoneEntry, teamEntry, spectateEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig2() {
        BlockEntry blockEntry = new BlockEntry(8000, true, 8000);
        ZoneEntry zoneEntry = new ZoneEntry(false, "", 1024, 1024, 1024, 1024);
        TeamEntry teamEntry = new TeamEntry(true, false);
        SpectateEntry spectateEntry = new SpectateEntry(true, false);

        RenderConfig renderConfig = new RenderConfig(2, "Max render", "#FFFFFFAA",
                blockEntry, zoneEntry, teamEntry, spectateEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig3() {
        BlockEntry blockEntry = new BlockEntry(4, false, 4);
        ZoneEntry zoneEntry = new ZoneEntry(false, "", 32, 32, 32, 32);
        TeamEntry teamEntry = new TeamEntry(true, false, "",
                false, true, 0.5F);
        SpectateEntry spectateEntry = new SpectateEntry(true, false, "",
                false, true, 0.5F, 20 * 5);

        RenderConfig renderConfig = new RenderConfig(3, "Better Performance", "#FFFFFFAA",
                blockEntry, zoneEntry, teamEntry, spectateEntry);

        return renderConfig.toJson();
    }
}
