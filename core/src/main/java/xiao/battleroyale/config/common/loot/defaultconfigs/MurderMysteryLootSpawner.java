package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.type.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.game.gamerule.defaultconfigs.MurderMysteryGamerule.MURDERER_ITEM_TAG;
import static xiao.battleroyale.config.common.game.gamerule.defaultconfigs.MurderMysteryGamerule.SURVIVOR_ITEM_TAG;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.MurderMystery256x256Zone.*;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class MurderMysteryLootSpawner {

    private static final String DEFAULT_FILE_NAME = "example_murdermystery_loot_spawner.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(addCommonLoot());
        lootSpawnerConfigsJson.add(addSurvivorLoot());
        lootSpawnerConfigsJson.add(addDetectiveLoot());
        lootSpawnerConfigsJson.add(addMurdererLoot());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    // 场景内物品
    public static JsonObject addCommonLoot() {
        ILootEntry abortEntry = new MultiEntry(Arrays.asList(
                new RandomEntry(0.004, new ItemEntry("minecraft:totem_of_undying")),
                new RepeatEntry(0, 14, new EmptyEntry(EmptyEntry.TYPE_ITEM)),
                new RandomEntry(0.1, new ItemEntry("minecraft:emerald"))
        ));
        LootConfigManager.LootConfig lootConfig = new LootConfigManager.LootConfig(0, "Common loot", "#FFFFFFAA", abortEntry);
        return lootConfig.toJson();
    }

    // 生存者物品
    public static JsonObject addSurvivorLoot() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                _repeatEmpty(8),
                commonSpyglass()
        ));
        LootConfigManager.LootConfig lootConfig = new LootConfigManager.LootConfig(SURVIVOR_ITEM_LOOT, "Survivor item loot", "#FFFFFFAA", multiEntry);
        return lootConfig.toJson();
    }
    // 侦探物品
    public static JsonObject addDetectiveLoot() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                detectiveDualWeapon(),
                _repeatEmpty(6),
                commonSpyglass()
        ));
        LootConfigManager.LootConfig lootConfig = new LootConfigManager.LootConfig(DETECTIVE_ITEM_LOOT, "Detective item loot", "#FFFFFFAA", multiEntry);
        return lootConfig.toJson();
    }
    // 杀手物品
    public static JsonObject addMurdererLoot() {
        ILootEntry multiEntry = new MultiEntry(Arrays.asList(
                murderDualWeapon(),
                _repeatEmpty(6),
                commonSpyglass()
        ));
        LootConfigManager.LootConfig lootConfig = new LootConfigManager.LootConfig(MURDERER_ITEM_LOOT, "Murderer item loot", "#FFFFFFAA", multiEntry);
        return lootConfig.toJson();
    }

    private static ILootEntry _repeatEmpty(int count) {
        return new RepeatEntry(count, count, new EmptyEntry(EmptyEntry.TYPE_ITEM));
    }

    private static ILootEntry commonSpyglass() {
        return new ItemEntry("minecraft:spyglass");
    }
    private static ILootEntry detectiveDualWeapon() {
        return new MultiEntry(Arrays.asList(
                // 随机抽把弓
                new WeightEntry(Arrays.asList(
                        // 弓
                        new WeightEntry.WeightedEntry(1, new ItemEntry("minecraft:bow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b}}}", SURVIVOR_ITEM_TAG))),
                        // 弩箭
                        new WeightEntry.WeightedEntry(1, new ItemEntry("minecraft:crossbow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b}}}", SURVIVOR_ITEM_TAG)))
                )),
                // 随机抽种箭
                new WeightEntry(Arrays.asList(
                        // 普通箭
                        new WeightEntry.WeightedEntry(3, new ItemEntry("minecraft:arrow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b}}}", SURVIVOR_ITEM_TAG), 30)),
                        // 光灵箭
                        new WeightEntry.WeightedEntry(2.5, new ItemEntry("minecraft:spectral_arrow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b}}}", SURVIVOR_ITEM_TAG), 25)),
                        // 减速箭
                        new WeightEntry.WeightedEntry(2, new ItemEntry("minecraft:tipped_arrow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:strong_slowness\"}}}", SURVIVOR_ITEM_TAG), 20)),
                        // 伤害箭
                        new WeightEntry.WeightedEntry(1, new ItemEntry("minecraft:tipped_arrow",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:strong_harming\"}}}", SURVIVOR_ITEM_TAG), 10))
                ))
        ));
    }
    private static ILootEntry murderDualWeapon() {
        return new MultiEntry(Arrays.asList(
                // 随机抽把武器
                new WeightEntry(Arrays.asList(
                        // 下界合金剑 (火剑)
                        new WeightEntry.WeightedEntry(3, new ItemEntry("minecraft:netherite_sword",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:enchantments\":{levels:{\"minecraft:fire_aspect\":2}}}}", MURDERER_ITEM_TAG))),
                        // 金斧 (秒人斧)
                        new WeightEntry.WeightedEntry(0.5, new ItemEntry("minecraft:golden_axe",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:enchantments\":{levels:{\"minecraft:sharpness\":18}}}}", MURDERER_ITEM_TAG)))
                )),
                // 随机抽个道具
                new WeightEntry(Arrays.asList(
                        // 喷溅加速药
                        new WeightEntry.WeightedEntry(3, new ItemEntry("minecraft:splash_potion",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:strong_swiftness\"}}}", MURDERER_ITEM_TAG))),
                        // 滞留治疗药
                        new WeightEntry.WeightedEntry(2.5, new ItemEntry("minecraft:lingering_potion",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:healing\"}}}", MURDERER_ITEM_TAG))),
                        // 滞留减速药 (泥)
                        new WeightEntry.WeightedEntry(2, new ItemEntry("minecraft:lingering_potion",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:slowness\"}}}", MURDERER_ITEM_TAG))),
                        // 滞留毒药 (燃烧瓶)
                        new WeightEntry.WeightedEntry(1, new ItemEntry("minecraft:lingering_potion",
                                String.format("{components:{\"minecraft:custom_data\":{%s:1b},\"minecraft:potion_contents\":{potion:\"minecraft:poison\"}}}", MURDERER_ITEM_TAG)))
                ))
        ));
    }
}
