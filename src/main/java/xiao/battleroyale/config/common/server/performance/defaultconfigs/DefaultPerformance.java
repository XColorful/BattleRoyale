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
        performanceConfigJson.add(generateDefaultPerformanceConfig3());
        writeJsonToFile(Paths.get(ServerConfigManager.get().getPerformanceConfigPath(), DEFAULT_FILE_NAME).toString(), performanceConfigJson);
    }

    private static JsonObject generateDefaultPerformanceConfig0() {
        // 默认性能配置:
        // 玩家数量: 10人, 模拟距离: 26, BFS处理Tick: 30秒, 立即下一次BFS: false, 空间系数: 0.75
        GeneratorEntry generatorEntry = GeneratorEntry.calculateRecommendedConfig(
                true, false,
                47,
                10, 26, 20 * 30, false, 0.75);

        PerformanceConfig performanceConfig = new PerformanceConfig(0, "Default performance", "#FFFFFF", true, generatorEntry);

        return performanceConfig.toJson();
    }

    private static JsonObject generateDefaultPerformanceConfig1() {
        // 最大质量配置:
        // 玩家数量: 20人, 模拟距离: 32, BFS处理Tick: 30秒, 立即下一次BFS: true, 空间系数: 1.0
        GeneratorEntry generatorEntry = GeneratorEntry.calculateRecommendedConfig(
                true, true,
                141,
                20, 32, 20 * 30, true, 1.0);

        PerformanceConfig performanceConfig = new PerformanceConfig(1, "Max quality", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }

    private static JsonObject generateDefaultPerformanceConfig2() {
        // 更好性能配置:
        // 玩家数量: 8人, 模拟距离: 16, BFS处理Tick: 30秒, 立即下一次BFS: false, 空间系数: 0.5
        GeneratorEntry generatorEntry = GeneratorEntry.calculateRecommendedConfig(
                false, false,
                15,
                8, 16, 20 * 30, false, 0.5);

        PerformanceConfig performanceConfig = new PerformanceConfig(2, "Better performance", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }

    private static JsonObject generateDefaultPerformanceConfig3() {
        // 数据均为目测，无重复实验，参考价值有限

        // tolerantCenterDist为10：
        /* 服务器（开远程桌面）
        i5-12600KF，内存条单32G，模拟距离64，-Xms 3G -Xms 3G
        */
        /* 服务器待机
        CPU 1% 1.02-1.15 GHz
        Memory use: 2350 - 1000 mb
        Avg tick: 0.266-0.292ms
        */
        /* 2玩家待机
        CPU 1% 1.06-1.27 GHz
        Avg tick: 4.7-5.0ms
        */
        /* 2玩家从米拉玛狮城-600 150 -7000开始一西一北创造飞行，飞到约x=-1659的城区记录均值峰值结束
        Avg tick: 8.0-10.6ms
        飞完待机Avg tick: 11.4-11.8ms
        */
        /* 在上一条件基础上开始游戏，以此配置持续刷新，并服务端每tick广播聊天栏消息
        Avg tick: 8+(?)-13.4ms
        飞完待机Avg tick: 12.3-13.0ms
        */
        // latest.log在6190行结束，含飞行和待机途中每tick的log

        // tolerantCenterDist为0：
        /* 服务器（开远程桌面）
        i5-12600KF，内存条单32G，模拟距离64，-Xms 3G -Xms 3G
         */
        /* 服务器待机
        CPU 1% 1.02-1.15 GHz
        Memory use: 2350 - 1000 mb
        Avg tick: 约0.29-0.33ms
         */
        /* 2玩家待机
        CPU 1% 1.11-1.61 GHz
        Memory use: 2500 - 730 mb
        Avg tick: 约4.7-5.2ms
         */
        /* 2玩家从米拉玛狮城600 150 -7000开始一西一北创造飞行，飞到约x=-1659的城区记录均值峰值结束
        Avg tick: 平原6.1-8.6ms 城区8.8-9.1ms
        飞完待机Avg tick: 12.0-12.3ms
         */
        /* 在上一条件基础上开始游戏，以此配置持续刷新，并服务端每tick广播聊天栏消息
        Avg tick: 7.3-9.8ms 9.0-11.2ms（这里峰值反而降了？）
        飞完待机Avg tick: 12.6-13.4ms
         */
        GeneratorEntry generatorEntry = new GeneratorEntry(true, true,
                500,
                500, 128, 0, 50000, 200000, 100, true, 300000, 10000);

        PerformanceConfig performanceConfig = new PerformanceConfig(3, "Developer Performance Test", "#FFFFFF", generatorEntry);

        return performanceConfig.toJson();
    }
}