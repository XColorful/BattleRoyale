package xiao.battleroyale.config.common.server.performance.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.performance.type.GeneratorEntry;

import java.nio.file.Paths;
import java.util.ArrayList;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CFHCPerformance {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_performance.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray performanceConfigJson = new JsonArray();
        performanceConfigJson.add(generateDefaultPerformance());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), performanceConfigJson);
    }

    private static JsonObject generateDefaultPerformance() {
        // 默认性能配置:
        // 玩家数量: 10人, 模拟距离: 16, BFS处理Tick: 5秒
        GeneratorEntry generatorEntry = new GeneratorEntry(true, true, false, false,
                new ArrayList<>(), new ArrayList<>(),
                false, new ArrayList<>(), new ArrayList<>(),
                2000, false,
                2000, 20, 3, 1500, 50000, 100, false, 100000, 10000);

        PerformanceConfigManager.PerformanceConfig performanceConfig = new PerformanceConfigManager.PerformanceConfig(0, "Default performance", "#FFFFFF", false, generatorEntry);

        return performanceConfig.toJson();
    }
}
