package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.EquipmentLevel;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CFHCLootSpawner {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_loot_spawner.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(addCombined());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    public static JsonObject addCombined() {
        RepeatEntry repeatEntry = new RepeatEntry(3, 6,
                new MultiEntry(Arrays.asList(
                        new WeightEntry(Arrays.asList(
                                new WeightedEntry(5, generateResources()),
                                new WeightedEntry(5, generateBattle()),
                                new WeightedEntry(5, generateFunction()),
                                new WeightedEntry(5, generateOthers())
                        )),
                        new RepeatEntry(0, 3, new EmptyEntry(LootEntryType.ITEM.getName()))
                ))
        );
        LootConfig lootConfig = new LootConfig(0, "CFHC common loot", "#FFFFFFAA", repeatEntry);
        return lootConfig.toJson();
    }

    /**
     * 物资
     */
    public static ILootEntry generateResources() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, generateMonsterLoot()),
                new WeightedEntry(10, generateBrew()),
                new WeightedEntry(35, generateBowResource()),
                new WeightedEntry(20, generateMineral())
        ));
    }
    public static ILootEntry generateMonsterLoot() { // 刷怪
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:blaze_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:magma_cube_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:ghast_spawn_egg", "", 1))
        ));
    }
    public static ILootEntry generateBrew() { // 酿造
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:nether_wart", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:glowstone_dust", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:blaze_powder", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:dragon_breath", "", 1))
        ));
    }
    public static ILootEntry generateBowResource() { // 射击
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:tripwire_hook", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:stick", "", 5)),
                new WeightedEntry(5, new ItemEntry("minecraft:flint", "", 5)),
                new WeightedEntry(5, new ItemEntry("minecraft:string", "", 5)),
                new WeightedEntry(5, new ItemEntry("minecraft:feather", "", 5))
        ));
    }
    public static ILootEntry generateMineral() { // 矿
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:iron_nugget", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:gold_nugget", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:amethyst_shard", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:quartz", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:emerald", "", 1))
        ));
    }

    /**
     * 战斗
     */
    public static ILootEntry generateBattle() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(35, generateEquipments()),
                new WeightedEntry(8, generateCall()),
                new WeightedEntry(15, generateEnchantments()),
                new WeightedEntry(3, generateSpecial()),
                new WeightedEntry(25, generateHorse())
        ));
    }
    public static ILootEntry generateEquipments() { // 防具
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new WeightEntry(Arrays.asList(
                        new WeightedEntry(10, EquipmentLevel.equipment(EquipmentLevel.GOLDEN, EquipmentLevel.HELMET, 5)),
                        new WeightedEntry(6, EquipmentLevel.equipment(EquipmentLevel.GOLDEN, EquipmentLevel.CHESTPLATE, 5)),
                        new WeightedEntry(8, EquipmentLevel.equipment(EquipmentLevel.GOLDEN, EquipmentLevel.LEGGINGS, 5)),
                        new WeightedEntry(12, EquipmentLevel.equipment(EquipmentLevel.GOLDEN, EquipmentLevel.BOOTS, 5))
                ))),
                new WeightedEntry(5, new WeightEntry(Arrays.asList(
                        new WeightedEntry(10, EquipmentLevel.equipment(EquipmentLevel.CHAINMAIL, EquipmentLevel.HELMET, 5)),
                        new WeightedEntry(6, EquipmentLevel.equipment(EquipmentLevel.CHAINMAIL, EquipmentLevel.CHESTPLATE, 5)),
                        new WeightedEntry(8, EquipmentLevel.equipment(EquipmentLevel.CHAINMAIL, EquipmentLevel.LEGGINGS, 5)),
                        new WeightedEntry(12, EquipmentLevel.equipment(EquipmentLevel.CHAINMAIL, EquipmentLevel.BOOTS, 5))
                ))),
                new WeightedEntry(15, new WeightEntry(Arrays.asList(
                        new WeightedEntry(10, EquipmentLevel.equipment(EquipmentLevel.LEATHER, EquipmentLevel.HELMET, 5)),
                        new WeightedEntry(6, EquipmentLevel.equipment(EquipmentLevel.LEATHER, EquipmentLevel.CHESTPLATE, 5)),
                        new WeightedEntry(8, EquipmentLevel.equipment(EquipmentLevel.LEATHER, EquipmentLevel.LEGGINGS, 5)),
                        new WeightedEntry(12, EquipmentLevel.equipment(EquipmentLevel.LEATHER, EquipmentLevel.BOOTS, 5))
                ))),
                new WeightedEntry(2, EquipmentLevel.equipment(EquipmentLevel.TURTLE, EquipmentLevel.HELMET, 5)),
                new WeightedEntry(2, new WeightEntry(Arrays.asList(
                        new WeightedEntry(35, new ItemEntry("minecraft:leather_horse_armor", "", 1)),
                        new WeightedEntry(25, new ItemEntry("minecraft:golden_horse_armor", "", 1)),
                        new WeightedEntry(15, new ItemEntry("minecraft:iron_horse_armor", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:diamond_horse_armor", "", 1))
                )))
        ));
    }
    public static ILootEntry generateCall() { // 召唤
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:snow_golem_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:iron_golem_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:vindicator_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:wither_skeleton_spawn_egg", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:warden_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:soul_sand", "", 1))
        ));
    }
    public static ILootEntry generateEnchantments() { // 附魔
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:lapis_lazuli", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:experience_bottle", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:book", "", 1))
        ));
    }
    public static ILootEntry generateSpecial() { // 特殊
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:elytra", "{Damage:402}", 1))
        ));
    }
    public static ILootEntry generateHorse() { // 骑兵
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:saddle", "", 1))
        ));
    }

    /**
     * 功能
     */
    public static ILootEntry generateFunction() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(10, generateBoat()),
                new WeightedEntry(35, generateTools()),
                new WeightedEntry(25, generateFunctionBlocks()),
                new WeightedEntry(15, generateLiving())
        ));
    }
    public static ILootEntry generateBoat() { // 船
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(1, new ItemEntry("minecraft:oak_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:spruce_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:birch_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:jungle_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:acacia_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:dark_oak_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:mangrove_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:cherry_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:bamboo_boat", "", 1)),
                new WeightedEntry(1, new ItemEntry("minecraft:minecart", "", 1))
        ));
    }
    public static ILootEntry generateTools() { // 工具
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:water_bucket", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:lava_bucket", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:powder_snow_bucket", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:fishing_rod", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:flint_and_steel", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:shears", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:brush", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:name_tag", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:lead", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:spyglass", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:carrot_on_a_stick", "", 1))
        ));
    }
    public static ILootEntry generateFunctionBlocks() { // 方块
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:magma_block", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:lightning_rod", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:beacon", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:damaged_anvil", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:scaffolding", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:enchanting_table", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:end_rod", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:flower_pot", "", 1))
        ));
    }
    public static ILootEntry generateLiving() { // 生物
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:donkey_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:horse_spawn_egg", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:mule_spawn_egg", "", 1))
        ));
    }

    /**
     * 杂物
     */
    public static ILootEntry generateOthers() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(45, generatePlanks()),
                new WeightedEntry(15, generateRedstone()),
                new WeightedEntry(35, generateOtherBlocks()),
                new WeightedEntry(25, generateTrash()),
                new WeightedEntry(5, generateHeads()),
                new WeightedEntry(15, generatePlants()),
                new WeightedEntry(5, generateMusic()),
                new WeightedEntry(15, generateMomentum())
        ));
    }
    public static ILootEntry generatePlanks() { // 木头
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:oak_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:spruce_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:birch_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:jungle_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:acacia_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:dark_oak_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:mangrove_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:cherry_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:crimson_planks", "", 16)),
                new WeightedEntry(5, new ItemEntry("minecraft:warped_planks", "", 16))
        ));
    }
    public static ILootEntry generateRedstone() { // 红石
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:redstone_block", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:hopper", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:repeater", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:comparator", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:piston", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:sticky_piston", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:dispenser", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:dropper", "", 4)),
                new WeightedEntry(5, new ItemEntry("minecraft:observer", "", 4))
        ));
    }
    public static ILootEntry generateOtherBlocks() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:pointed_dripstone", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:glow_lichen", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:cobweb", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:slime_block", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:honey_block", "", 1))
        ));
    }
    public static ILootEntry generateTrash() { // (近似)垃圾
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:snowball", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:egg", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:leather", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:scute", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:bowl", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:wheat", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:firework_star", "", 3))
        ));
    }
    public static ILootEntry generateHeads() { // 头颅
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:skeleton_skull", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:wither_skeleton_skull", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:player_head", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:zombie_head", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:creeper_head", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:piglin_head", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:dragon_head", "", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:carved_pumpkin", "", 1))
        ));
    }
    public static ILootEntry generatePlants() { // 植物
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:sunflower", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:lilac", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:rose_bush", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:peony", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:pitcher_plant", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:large_fern", "", 3)),
                new WeightedEntry(5, new ItemEntry("minecraft:tall_grass", "", 3))
        ));
    }
    public static ILootEntry generateMusic() { // 音乐
        return new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:jukebox", "", 1),
                new WeightEntry(Arrays.asList(
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_otherside", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_13", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_cat", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_blocks", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_chirp", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_far", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_mall", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_mellohi", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_stal", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_strad", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_ward", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_11", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_wait", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_5", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_pigstep", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_relic", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_", "", 1)),
                        new WeightedEntry(5, new ItemEntry("minecraft:music_disc_", "", 1))
                ))
        ));
    }
    public static ILootEntry generateMomentum() { // 气势
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:ponder_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:sing_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:seek_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:feel_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:admire_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:call_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:yearn_goat_horn\"}", 1)),
                new WeightedEntry(5, new ItemEntry("minecraft:goat_horn", "{instrument:\"minecraft:dream_goat_horn\"}", 1))
        ));
    }
}
