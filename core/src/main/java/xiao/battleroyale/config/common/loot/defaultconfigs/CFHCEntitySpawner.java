package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.EquipmentLevel;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CFHCEntitySpawner {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_entity_spawner.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray entitySpawnerConfigsJson = new JsonArray();
        entitySpawnerConfigsJson.add(addBombZoneEntity());
        entitySpawnerConfigsJson.add(addPostBombZoneEntity());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), entitySpawnerConfigsJson);
    }

    // 轰炸区
    public static JsonObject addBombZoneEntity() {
        ILootEntry entityEntry = new EntityEntry("minecraft:tnt", "", 5, 0);

        LootConfig lootConfig = new LootConfig(1, "TNT bomb zone", "#FFFFFFAA", entityEntry);

        return lootConfig.toJson();
    }

    // 空投+刷怪
    public static JsonObject addPostBombZoneEntity() {
        ILootEntry entityEntry = new MultiEntry(Arrays.asList(
                new ExtraEntry(false, true,
                        new RandomEntry(0.1, generateRareLoot()),
                        generateRareLootPost()),
                generateCommonLoot(),
                new ItemEntry("minecraft:end_stone", "", 64).toEntityEntry()
        ));

        LootConfig lootConfig = new LootConfig(2, "Bonus zone", "#FFFFFFAA", entityEntry);

        return lootConfig.toJson();
    }

    private static ILootEntry generateRareLoot() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:totem_of_undying", "", 1).toEntityEntry()),
                new WeightedEntry(5, EquipmentLevel.equipment(EquipmentLevel.NETHERITE, EquipmentLevel.HELMET, 7, 5).toEntityEntry()),
                new WeightedEntry(5, new ItemEntry("minecraft:potion", "{Potion:\"minecraft:regeneration\"}", 1).toEntityEntry()),
                new WeightedEntry(15, new ItemEntry("minecraft:golden_apple", "", 1).toEntityEntry()),
                new WeightedEntry(20, new ItemEntry("minecraft:golden_carrot", "" ,1).toEntityEntry())
        ));
    }
    private static ILootEntry generateRareLootPost() {
        return new MultiEntry(Arrays.asList(
                new MessageEntry(true, true, "Rare loot generated", "#FF0000"),
                new EntityEntry("minecraft:vindicator", "", 1, 5),
                new EntityEntry("minecraft:ravager", "", 1, 5),
                new EntityEntry("minecraft:pillager", "", 1, 5),
                new ItemEntry("minecraft:purpur_block", "", 64).toEntityEntry()
        ));
    }

    private static ILootEntry generateCommonLoot() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new WeightEntry(Arrays.asList(
                        new WeightedEntry(10, EquipmentLevel.equipment(EquipmentLevel.DIAMOND, EquipmentLevel.HELMET, 5).toEntityEntry()),
                        new WeightedEntry(6, EquipmentLevel.equipment(EquipmentLevel.DIAMOND, EquipmentLevel.CHESTPLATE, 5).toEntityEntry()),
                        new WeightedEntry(8, EquipmentLevel.equipment(EquipmentLevel.DIAMOND, EquipmentLevel.CHESTPLATE, 5).toEntityEntry()),
                        new WeightedEntry(12, EquipmentLevel.equipment(EquipmentLevel.DIAMOND, EquipmentLevel.LEGGINGS, 5).toEntityEntry())
                ))),
                new WeightedEntry(10, new WeightEntry(Arrays.asList(
                        new WeightedEntry(10, EquipmentLevel.equipment(EquipmentLevel.IRON, EquipmentLevel.HELMET, 10).toEntityEntry()),
                        new WeightedEntry(6, EquipmentLevel.equipment(EquipmentLevel.IRON, EquipmentLevel.CHESTPLATE, 10).toEntityEntry()),
                        new WeightedEntry(8, EquipmentLevel.equipment(EquipmentLevel.IRON, EquipmentLevel.CHESTPLATE, 10).toEntityEntry()),
                        new WeightedEntry(12, EquipmentLevel.equipment(EquipmentLevel.IRON, EquipmentLevel.LEGGINGS, 10).toEntityEntry())
                ))),
                new WeightedEntry(15, new ItemEntry("minecraft:diamond_sword", "", 1).toEntityEntry()),
                new WeightedEntry(10, new ItemEntry("minecraft:iron_sword", "", 1).toEntityEntry()),
                new WeightedEntry(15, new ItemEntry("minecraft:potion", "{Potion:\"minecraft:healing\"}", 1).toEntityEntry())
        ));
    }
}
