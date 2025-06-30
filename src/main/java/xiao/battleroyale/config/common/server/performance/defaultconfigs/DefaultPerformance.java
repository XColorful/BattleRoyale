package xiao.battleroyale.config.common.server.performance.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager.PerformanceConfig;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultPerformance {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs() {
        JsonArray performanceConfigJson = new JsonArray();
        performanceConfigJson.add(generateDefaultPerformanceConfig0());
        performanceConfigJson.add(generateDefaultPerformanceConfig1());
        performanceConfigJson.add(generateDefaultPerformanceConfig2());
        writeJsonToFile(Paths.get(ServerConfigManager.get().getPerformanceConfigPath(), DEFAULT_FILE_NAME).toString(), performanceConfigJson);
    }

    private static JsonObject generateDefaultPerformanceConfig0() {
        GeneratorEntry generatorEntry = new GeneratorEntry(true, false,
                150,
                150, 26, 3, 500, 512*50, 20*10, false, 5000, 2000);

        PerformanceConfig performanceConfig = new PerformanceConfig(0, "Default performance", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }

    private static JsonObject generateDefaultPerformanceConfig1() {
        GeneratorEntry generatorEntry = new GeneratorEntry(true, true,
                512,
                512, 32, 3, 500, 1024*100, 20*5, true, 8000, 3000);

        PerformanceConfig performanceConfig = new PerformanceConfig(1, "Max quality", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }

    private static JsonObject generateDefaultPerformanceConfig2() {
        GeneratorEntry generatorEntry = new GeneratorEntry(false, false,
                5,
                50, 16, 3, 500, 128*10, 20*15, false, 3000, 1000);

        PerformanceConfig performanceConfig = new PerformanceConfig(2, "Better performance", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }
}
