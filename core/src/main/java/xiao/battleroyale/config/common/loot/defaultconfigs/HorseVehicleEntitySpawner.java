package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.EntityEntry;
import xiao.battleroyale.config.common.loot.type.RandomEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.ENTITY_SPAWNER;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class HorseVehicleEntitySpawner {

    private static final String DEFAULT_FILE_NAME = "example_horse_vehicle.json";

    public static void generateDefaultConfigs() {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(generateHorseVehicle());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(ENTITY_SPAWNER), LootConfigManager.ENTITY_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), entitySpawnerConfigsJson);
    }

    private static JsonObject generateHorseVehicle() {
        WeightEntry horseTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(35, generateCamel()),
                new WeightedEntry(15, generate60kmhHorse()),
                new WeightedEntry(30, generate30kmhDonkey()),
                new WeightedEntry(20, generate40kmhMule())
        ));
        RandomEntry randomEntry = new RandomEntry(0.8,
                horseTypeWeight);

        LootConfig lootConfig = new LootConfig(0, "Horse Vehicle", "#FFFFFFAA",
                randomEntry);

        return lootConfig.toJson();
    }

    private static ILootEntry generateCamel() {
        // 骆驼作为特殊载具，保持原样，只带鞍，不修改属性，不能装箱子
        return new EntityEntry("minecraft:camel", "{Tame:1b,SaddleItem:{id:\"minecraft:saddle\",Count:1b}}", 1, 5);
    }

    private static ILootEntry generate60kmhHorse() {
        // 马：速度60km/h=0.833，血量20，跳跃力量1.0，钻石马铠带保护X
        return new EntityEntry("minecraft:horse", "{Tame:1b,SaddleItem:{id:\"minecraft:saddle\",Count:1b},ArmorItem:{id:\"minecraft:diamond_horse_armor\",Count:1b,tag:{Enchantments:[{id:\"minecraft:protection\",lvl:10s}]}},Attributes:[{Name:\"minecraft:generic.movement_speed\",Base:0.8333333d},{Name:\"minecraft:generic.max_health\",Base:20.0d},{Name:\"minecraft:horse.jump_strength\",Base:1.0d}]}", 1, 5);
    }

    // NBT标签：Items必须在ChestedHorse之前，否则不会有箱子

    private static ILootEntry generate30kmhDonkey() {
        // 驴：速度30km/h=0.41666，血量50，跳跃高度3.5 (jump_strength约为0.8)
        return new EntityEntry("minecraft:donkey", "{Tame:1b,Items:[],ChestedHorse:1b,SaddleItem:{id:\"minecraft:saddle\",Count:1b},Attributes:[{Name:\"minecraft:generic.movement_speed\",Base:0.4166666d},{Name:\"minecraft:generic.max_health\",Base:50.0d},{Name:\"minecraft:horse.jump_strength\",Base:0.8d}]}", 1, 5);
    }

    private static ILootEntry generate40kmhMule() {
        // 骡子：速度40km/h=0.555555，血量40，跳跃高度2.5 (jump_strength约为0.7)
        return new EntityEntry("minecraft:mule", "{Tame:1b,Items:[],ChestedHorse:1b,SaddleItem:{id:\"minecraft:saddle\",Count:1b},Attributes:[{Name:\"minecraft:generic.movement_speed\",Base:0.5555555d},{Name:\"minecraft:generic.max_health\",Base:40.0d},{Name:\"minecraft:horse.jump_strength\",Base:0.7d}]}", 1, 5);
    }
}