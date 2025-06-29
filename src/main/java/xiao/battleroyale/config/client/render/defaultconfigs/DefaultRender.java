package xiao.battleroyale.config.client.render.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager.RenderConfig;
import xiao.battleroyale.config.client.render.type.BlockEntry;
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
        writeJsonToFile(Paths.get(ClientConfigManager.get().getRenderConfigPath(), DEFAULT_FILE_NAME).toString(), renderConfigJson);
    }

    private static JsonObject generateDefaultRenderConfig0() {
        BlockEntry blockEntry = new BlockEntry(16);
        ZoneEntry zoneEntry = new ZoneEntry(false);

        RenderConfig renderConfig = new RenderConfig(0, "No limit", "#FFFFFFAA", blockEntry, zoneEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig1() {
        BlockEntry blockEntry = new BlockEntry(0);
        ZoneEntry zoneEntry = new ZoneEntry(true, "#0000FF");

        RenderConfig renderConfig = new RenderConfig(1, "No render", "#FFFFFFAA", blockEntry, zoneEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig2() {
        BlockEntry blockEntry = new BlockEntry(8000);
        ZoneEntry zoneEntry = new ZoneEntry(false);

        RenderConfig renderConfig = new RenderConfig(2, "Max render", "#FFFFFFAA", blockEntry, zoneEntry);

        return renderConfig.toJson();
    }

    private static JsonObject generateDefaultRenderConfig3() {
        BlockEntry blockEntry = new BlockEntry(4);
        ZoneEntry zoneEntry = new ZoneEntry(false);

        RenderConfig renderConfig = new RenderConfig(3, "Better Performance", "#FFFFFFAA", blockEntry, zoneEntry);

        return renderConfig.toJson();
    }
}
