package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.LOOT_SPAWNER;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class TaczLootSpawner {

    private static final String DEFAULT_FILE_NAME = "example_tacz_1.1.6.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateTaczCommonLoot());
        lootSpawnerConfigsJson.add(generateTaczRareLoot());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(LOOT_SPAWNER), LootConfigManager.LOOT_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    /**
     * 重复2-3次
     * 物品大类（武器，枪配件，子弹……）
     * 物品小类（武器：步枪，栓狙，连狙……）
     * 物品权重（步枪：猛男，AK，M4）
     * 多个物品（子弹+猛男）
     */
    private static JsonObject generateTaczCommonLoot() {
        WeightEntry itemTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(32, commonWeaponEntry()),
                new WeightedEntry(22, commonAttachmentEntry()),
                new WeightedEntry(8, commonAmmoEntry())
        ));
        RepeatEntry repeatEntry = new RepeatEntry(2, 3, itemTypeWeight);

        MultiEntry multiEntry = new MultiEntry(Arrays.asList(
                itemTypeWeight,
                repeatEntry,
                minecraftItemEntry(),
                specialItemEntry()
        ));

        LootConfig lootConfig = new LootConfig(0, "Widespread common loot", "#FFFFFFAA",
                multiEntry);

        return lootConfig.toJson();
    }

    private static JsonObject generateTaczRareLoot() {
        MultiEntry itemList = new MultiEntry(Arrays.asList(
                rareWeaponEntry(),
                rareAttachmentEntry(),
                rareAmmoEntry()
        ));

        RepeatEntry repeatEntry = new RepeatEntry(2, 2, itemList);

        LootConfig lootConfig = new LootConfig(1, "Rare high-tier loots", "#FFFFFFAA",
                repeatEntry);

        return lootConfig.toJson();
    }

    private static ILootEntry minecraftItemEntry() {
        ItemEntry dinner = new ItemEntry("minecraft:cooked_chicken", "{display:{Name:'{\"text\":\"Winner Chicken Dinner\",\"color\":\"gold\"}'}}", 1);
        ItemEntry totem = new ItemEntry("totem_of_undying", "{}", 1);
        ItemEntry gApple = new ItemEntry("minecraft:golden_apple", "{}", 1);
        ItemEntry pearl = new ItemEntry("minecraft:ender_pearl", "{}", 1);
        ItemEntry heal = new ItemEntry("minecraft:splash_potion", "{Potion:\"minecraft:healing\",CustomPotionEffects:[{Id:12,Duration:100}]}", 1);
        ItemEntry harm = new ItemEntry("minecraft:splash_potion", "{Potion:\"minecraft:strong_harming\"}", 1);
        ItemEntry milk = new ItemEntry("minecraft:milk_bucket", "{}", 1);
        ItemEntry poisonArrow = new ItemEntry("minecraft:tipped_arrow", "{Potion:\"minecraft:long_poison\"}", 5);
        ItemEntry spectralArrow = new ItemEntry("minecraft:spectral_arrow", "{}", 1);
        ItemEntry slowArrow = new ItemEntry("minecraft:tipped_arrow", "{Potion:\"minecraft:long_slowness\"}", 5);
        ItemEntry harmArrow = new ItemEntry("minecraft:tipped_arrow", "{Potion:\"minecraft:strong_harming\"}", 5);
        ItemEntry crossbow = new ItemEntry("minecraft:crossbow", "{}", 1);
        WeightEntry itemTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(1, dinner),
                new WeightedEntry(2, totem),
                new WeightedEntry(7, gApple),
                new WeightedEntry(15, pearl),
                new WeightedEntry(5, heal),
                new WeightedEntry(5, harm),
                new WeightedEntry(5, milk),
                new WeightedEntry(5, poisonArrow),
                new WeightedEntry(5, spectralArrow),
                new WeightedEntry(5, slowArrow),
                new WeightedEntry(5, harmArrow),
                new WeightedEntry(25, crossbow)
        ));
        return new RandomEntry(0.1, itemTypeWeight);
    }

    private static ILootEntry specialItemEntry() {
        // 机枪
        MultiEntry minigun = gunAmmoBuilder("minigun", "308", 48);
        // 火箭筒
        MultiEntry rpg = gunAmmoBuilder("rpg7", "rpg_rocker", 1);
        // MC稀有物品
        ItemEntry gApplePlus = new ItemEntry("minecraft:enchanted_golden_apple", "{}", 1);
        WeightEntry itemTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(5, minigun),
                new WeightedEntry(5, rpg),
                new WeightedEntry(5, gApplePlus)
        ));
        // 开局3分钟内不刷新神器
        return new TimeEntry(20 * 180, Integer.MAX_VALUE / 2, new RandomEntry(1 / 300F, itemTypeWeight));
    }

    private static ILootEntry commonWeaponEntry() {
        WeightEntry weaponTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(25, commonAREntry()),
                new WeightedEntry(11, commonSREntry()),
                new WeightedEntry(12, commonDMREntry()),
                new WeightedEntry(13, commonShotgunEntry()),
                new WeightedEntry(25, commonSMGEntry()),
                new WeightedEntry(8, commonPistolEntry()),
                new WeightedEntry(6, commonMeleeEntry())
        ));

        return weaponTypeWeight;
    }

    private static ILootEntry rareWeaponEntry() {
        WeightEntry weaponTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(15, rareAREntry()),
                new WeightedEntry(5, rareSREntry()),
                new WeightedEntry(10, rareDMREntry()),
                new WeightedEntry(5, rareSMGEntry())
        ));
        return weaponTypeWeight;
    }

    private static MultiEntry gunAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun", "{GunId:\"tacz:" + gunName + "\"}", 1),
                ammoBuilder(ammoName, ammoCount)
        ));
    }

    private static ILootEntry commonAREntry() {
        MultiEntry _811Ammo = gunAmmoBuilder("type_81", "762x39", 30);
        MultiEntry akmAmmo = gunAmmoBuilder("ak47", "762x39", 30);
        MultiEntry m16a4Ammo = gunAmmoBuilder("m16a4", "556x45", 30);
        MultiEntry scarlAmmo = gunAmmoBuilder("scar_l", "556x45", 30);
        MultiEntry qbzAmmo = gunAmmoBuilder("qbz_95", "58x42", 30);
        MultiEntry m416Ammo = gunAmmoBuilder("hk416d", "556x45", 30);
        MultiEntry g36kAmmo = gunAmmoBuilder("g36k", "556x45", 30);
        MultiEntry m16a1Ammo = gunAmmoBuilder("m16a1", "556x45", 20);
        MultiEntry augAmmo = gunAmmoBuilder("aug", "556x45", 30);
        MultiEntry m4a1Ammo = gunAmmoBuilder("m4a1", "556x45", 30);
        MultiEntry rpk = gunAmmoBuilder("rpk", "762x39", 40);
        MultiEntry m249 = gunAmmoBuilder("m249", "556x45", 40);

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(2, m249),
                new WeightedEntry(3, rpk),
                new WeightedEntry(5, _811Ammo),
                new WeightedEntry(5, akmAmmo),
                new WeightedEntry(10, m16a4Ammo),
                new WeightedEntry(15, scarlAmmo),
                new WeightedEntry(15, qbzAmmo),
                new WeightedEntry(15, m416Ammo),
                new WeightedEntry(20, g36kAmmo),
                new WeightedEntry(20, m16a1Ammo),
                new WeightedEntry(20, augAmmo),
                new WeightedEntry(20, m4a1Ammo)
        ));
    }

    private static ILootEntry rareAREntry() {
        MultiEntry scarhAmmo = gunAmmoBuilder("scar_h", "308", 20);
        MultiEntry g3Ammo = gunAmmoBuilder("hk_g3", "308", 20);
        MultiEntry _811Ammo = gunAmmoBuilder("type_81", "762x39", 30);
        MultiEntry akmAmmo = gunAmmoBuilder("ak47", "762x39", 30);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, scarhAmmo),
                new WeightedEntry(5, g3Ammo),
                new WeightedEntry(10, _811Ammo),
                new WeightedEntry(10, akmAmmo)
        ));
    }

    private static ILootEntry commonSREntry() {
        MultiEntry m24Ammo = gunAmmoBuilder("m700", "30_06", 5);
        MultiEntry springAmmo = gunAmmoBuilder("springfield1873", "45_70", 5); // 这什么鸟枪只能装一发子弹
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, m24Ammo),
                new WeightedEntry(40, springAmmo)
        ));
    }

    private static ILootEntry rareSREntry() {
        MultiEntry m95Ammo = gunAmmoBuilder("m95", "50bmg", 5);
        MultiEntry m107Ammo = gunAmmoBuilder("m107", "50bmg", 10);
        MultiEntry awmAmmo = gunAmmoBuilder("ai_awp", "338", 5);
        MultiEntry m24Ammo = gunAmmoBuilder("m700", "30_06", 5);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, m95Ammo),
                new WeightedEntry(10, m107Ammo),
                new WeightedEntry(30, awmAmmo),
                new WeightedEntry(40, m24Ammo)
        ));
    }

    private static ILootEntry commonDMREntry() {
        MultiEntry sksAmmo = gunAmmoBuilder("sks_tactical", "762x39", 10);
        MultiEntry sprAmmo = gunAmmoBuilder("spr15hb", "556x45", 15);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(20, sksAmmo),
                new WeightedEntry(20, sprAmmo)
        ));
    }

    private static ILootEntry rareDMREntry() {
        MultiEntry mk14Ammo = gunAmmoBuilder("mk14", "308", 10);
        MultiEntry sksAmmo = gunAmmoBuilder("sks_tactical", "762x39", 10);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, mk14Ammo),
                new WeightedEntry(10, sksAmmo)
        ));
    }

    private static ILootEntry commonShotgunEntry() {
        MultiEntry s1897Ammo = gunAmmoBuilder("m870", "12g", 5);
        MultiEntry s12kAmmo = gunAmmoBuilder("aa12", "12g", 7);
        MultiEntry s686Ammo = gunAmmoBuilder("db_long", "12g", 2);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, s1897Ammo),
                new WeightedEntry(40, s686Ammo),
                new WeightedEntry(30, s12kAmmo)
        ));
    }

    private static ILootEntry commonSMGEntry() {
        MultiEntry ump45Ammo = gunAmmoBuilder("ump45", "45acp", 25);
        MultiEntry vectorAmmo = gunAmmoBuilder("vector45", "45acp", 20);
        MultiEntry uziAmmo = gunAmmoBuilder("uzi", "9mm", 20);
        MultiEntry mp5Ammo = gunAmmoBuilder("hk_mp5a5", "9mm", 30);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(20, ump45Ammo),
                new WeightedEntry(15, vectorAmmo),
                new WeightedEntry(15, uziAmmo),
                new WeightedEntry(10, mp5Ammo)
        ));
    }

    private static ILootEntry rareSMGEntry() {
        MultiEntry p90Ammo = gunAmmoBuilder("p90", "57x28", 50);
        MultiEntry vectorAmmo = gunAmmoBuilder("vector45", "45acp", 20);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, p90Ammo),
                new WeightedEntry(10, vectorAmmo)
        ));
    }

    private static ILootEntry commonPistolEntry() {
        MultiEntry sawedAmmo = gunAmmoBuilder("db_short", "12g", 2);
        MultiEntry deagleAmmo = gunAmmoBuilder("deagle", "50ae", 7);
        MultiEntry deagleGAmmo = gunAmmoBuilder("deagle_golden", "357mag", 9);
        MultiEntry m1911Ammo = gunAmmoBuilder("m1911", "45acp", 7);
        MultiEntry p320Ammo = gunAmmoBuilder("p320", "45acp", 12);
        MultiEntry b93rAmmo = gunAmmoBuilder("b93r", "9mm", 20);
        MultiEntry glockAmmo = gunAmmoBuilder("glock_17", "9mm", 17);
        MultiEntry cz75Ammo = gunAmmoBuilder("cz75", "9mm", 16);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, sawedAmmo),
                new WeightedEntry(5, cz75Ammo),
                new WeightedEntry(10, deagleAmmo),
                new WeightedEntry(10, deagleGAmmo),
                new WeightedEntry(10, m1911Ammo),
                new WeightedEntry(10, p320Ammo),
                new WeightedEntry(15, b93rAmmo),
                new WeightedEntry(15, glockAmmo)
        ));
    }

    private static ILootEntry commonMeleeEntry() {
        ItemEntry trident = new ItemEntry("trident", "{}", 1);
        ItemEntry shield = new ItemEntry("shield", "{}", 1);
        ItemEntry diamondSword = new ItemEntry("minecraft:diamond_sword", "{}", 1);
        ItemEntry diamondAxe = new ItemEntry("minecraft:diamond_axe", "{}", 1);
        ItemEntry ironSword = new ItemEntry("minecraft:iron_sword", "{}", 1);
        ItemEntry ironAxe = new ItemEntry("minecraft:iron_axe", "{}", 1);
        ItemEntry stoneSword = new ItemEntry("minecraft:stone_sword", "{}", 1);
        ItemEntry stoneAxe = new ItemEntry("minecraft:stone_axe", "{}", 1);
        ItemEntry woodenSword = new ItemEntry("minecraft:wooden_sword", "{}", 1);
        ItemEntry woodenAxe = new ItemEntry("minecraft:wooden_axe", "{}", 1);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(2, trident),
                new WeightedEntry(2, shield),
                new WeightedEntry(3, diamondSword),
                new WeightedEntry(3, diamondAxe),
                new WeightedEntry(10, ironSword),
                new WeightedEntry(10, ironAxe),
                new WeightedEntry(15, stoneSword),
                new WeightedEntry(15, stoneAxe),
                new WeightedEntry(20, woodenSword),
                new WeightedEntry(20, woodenAxe)
        ));
    }

    private static ILootEntry commonAttachmentEntry() {
        WeightEntry attachmentTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(24, commonMuzzleEntry()),
                new WeightedEntry(23, commonMagazineEntry()),
                new WeightedEntry(10, commonStockEntry()),
                new WeightedEntry(7, commonGripEntry()),
                new WeightedEntry(33, commonScopeEntry()),
                new WeightedEntry(3, commonLaserEntry())
        ));
        return attachmentTypeWeight;
    }

    private static ILootEntry rareAttachmentEntry() {
        WeightEntry attchmentTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(30, rareMagazineEntry()),
                new WeightedEntry(30, rareScopeEntry())
        ));
        return attchmentTypeWeight;
    }

    private static ILootEntry commonMuzzleEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(75, commonCompensatorEntry()),
                new WeightedEntry(50, commonSilencerEntry())
        ));
    }

    // 补偿+制退器+刺刀
    private static ILootEntry commonCompensatorEntry() {
        ItemEntry trident = attachmentBuilder("muzzle_compensator_trident");
        ItemEntry k7 = attachmentBuilder("muzzle_brake_cthulhu");
        ItemEntry a3 = attachmentBuilder("muzzle_brake_pioneer");
        ItemEntry d2 = attachmentBuilder("muzzle_brake_cyclone_d2");
        ItemEntry trex = attachmentBuilder("muzzle_brake_trex");

        ItemEntry m9 = attachmentBuilder("bayonet_m9");

        ItemEntry deagleG = attachmentBuilder("deagle_golden_long_barrel");
        ItemEntry _6h3 = attachmentBuilder("bayonet_6h3");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(10, trident),
                new WeightedEntry(10, k7),
                new WeightedEntry(10, a3),
                new WeightedEntry(10, d2),
                new WeightedEntry(15, trex),
                new WeightedEntry(10, deagleG),
                new WeightedEntry(10, _6h3)
        ));
    }

    private static ILootEntry commonSilencerEntry() {
        ItemEntry s1 = attachmentBuilder("muzzle_silencer_phantom_s1");
        ItemEntry ptil = attachmentBuilder("muzzle_silencer_ptilopsis");

        ItemEntry vulture = attachmentBuilder("muzzle_silencer_vulture");
        ItemEntry knight = attachmentBuilder("muzzle_silencer_knight_qd");
        ItemEntry ursus = attachmentBuilder("muzzle_silencer_ursus");
        ItemEntry mirage = attachmentBuilder("muzzle_silencer_mirage");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(20, s1),
                new WeightedEntry(35, ptil),
                new WeightedEntry(4, vulture),
                new WeightedEntry(4, knight),
                new WeightedEntry(4, ursus),
                new WeightedEntry(8, mirage)
        ));
    }

    private static ILootEntry commonMagazineEntry() {
        ItemEntry srMag2 = attachmentBuilder("sniper_extended_mag_2");
        ItemEntry srMag1 = attachmentBuilder("sniper_extended_mag_1");
        ItemEntry heavy3 = attachmentBuilder("extended_mag_3");
        ItemEntry heavy2 = attachmentBuilder("extended_mag_2");
        ItemEntry heavy1 = attachmentBuilder("extended_mag_1");
        ItemEntry light3 = attachmentBuilder("light_extended_mag_3");
        ItemEntry light2 = attachmentBuilder("light_extended_mag_2");
        ItemEntry light1 = attachmentBuilder("light_extended_mag_1");
        return new WeightEntry(Arrays.asList(
                // 5
                new WeightedEntry(1.5, srMag2),
                new WeightedEntry(3.5, srMag1),
                // 15
                new WeightedEntry(4, heavy3),
                new WeightedEntry(6, heavy2),
                new WeightedEntry(8, heavy1),
                // 25
                new WeightedEntry(5, light3),
                new WeightedEntry(7, light2),
                new WeightedEntry(10, light1)
        ));
    }

    private static ILootEntry rareMagazineEntry() {
        ItemEntry i = attachmentBuilder("ammo_mod_i");
        ItemEntry hp = attachmentBuilder("ammo_mod_hp");
        ItemEntry he = attachmentBuilder("ammo_mod_he");
        ItemEntry fmj = attachmentBuilder("ammo_mod_fmj");
        ItemEntry srMag3 = attachmentBuilder("sniper_extended_mag_3");
        ItemEntry light3 = attachmentBuilder("light_extended_mag_3");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, i),
                new WeightedEntry(5, hp),
                new WeightedEntry(5, he),
                new WeightedEntry(5, fmj),
                new WeightedEntry(10, srMag3),
                new WeightedEntry(10, light3)
        ));
    }

    private static ILootEntry commonStockEntry() {
        ItemEntry ak12 = attachmentBuilder("stock_ak12");
        ItemEntry c5 = attachmentBuilder("stock_carbon_bone_c5");
        ItemEntry moe = attachmentBuilder("stock_moe");
        ItemEntry ctr = attachmentBuilder("stock_tactical_ar");
        ItemEntry ks = attachmentBuilder("stock_hk_slim_line");
        ItemEntry b5 = attachmentBuilder("stock_militech_b5");
        ItemEntry m4ss = attachmentBuilder("stock_m4ss");
        ItemEntry rs = attachmentBuilder("stock_ripstock");

        ItemEntry oemTactical = attachmentBuilder("oem_stock_tactical");
        ItemEntry oemLight = attachmentBuilder("oem_stock_light");
        ItemEntry oemHeavy = attachmentBuilder("oem_stock_heavy");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(4, ak12),
                new WeightedEntry(4, c5),
                new WeightedEntry(4, moe),
                new WeightedEntry(4, ctr),
                new WeightedEntry(4, ks),
                new WeightedEntry(4, b5),
                new WeightedEntry(4, m4ss),
                new WeightedEntry(4, rs),
                new WeightedEntry(11, oemTactical),
                new WeightedEntry(11, oemLight),
                new WeightedEntry(11, oemHeavy)
        ));
    }

    private static ILootEntry commonGripEntry() {
        ItemEntry rk_0 = attachmentBuilder("grip_rk0");
        ItemEntry sg2 = attachmentBuilder("grip_vertical_talon");
        ItemEntry military = attachmentBuilder("grip_vertical_military");
        ItemEntry ranger = attachmentBuilder("grip_vertical_ranger");
        ItemEntry se_5 = attachmentBuilder("grip_se_5");

        ItemEntry afg2 = attachmentBuilder("grip_magpul_afg_2");
        ItemEntry p_2 = attachmentBuilder("grip_osovets_black");
        ItemEntry rk_1 = attachmentBuilder("grip_rk1_b25u");
        ItemEntry td = attachmentBuilder("grip_td");
        ItemEntry rk_6 = attachmentBuilder("grip_rk6");
        ItemEntry si = attachmentBuilder("grip_cobra");
        return new WeightEntry(Arrays.asList(
                // 15 垂握
                new WeightedEntry(3, rk_0),
                new WeightedEntry(3, sg2),
                new WeightedEntry(3, military),
                new WeightedEntry(3, ranger),
                new WeightedEntry(3, se_5),
                // 20
                new WeightedEntry(3, afg2),
                new WeightedEntry(3, p_2),
                new WeightedEntry(3, rk_1),
                new WeightedEntry(3, td),
                new WeightedEntry(4, rk_6),
                new WeightedEntry(4, si)
        ));
    }

    private static ItemEntry attachmentBuilder(String attachmentName) {
        return new ItemEntry("tacz:attachment", "{AttachmentId:\"tacz:" + attachmentName+ "\"}", 1);
    }

    private static ILootEntry commonScopeEntry() {
        ItemEntry scope10x = attachmentBuilder("scope_standard_8x");
        ItemEntry scope6_5x = attachmentBuilder("scope_vudu");
        ItemEntry scope6_25x = attachmentBuilder("scope_lpvo_1_6");
        ItemEntry scope4_25x2 = attachmentBuilder("scope_contender");
        ItemEntry scope4_25x = attachmentBuilder("scope_elcan_4x");
        ItemEntry scope3_25x = attachmentBuilder("scope_hamr");
        ItemEntry scope2_5x = attachmentBuilder("scope_acog_ta31");
        ItemEntry scope2x_552 = attachmentBuilder("sight_552");
        ItemEntry scope2x_exp3 = attachmentBuilder("sight_exp3");
        ItemEntry scope2x_uh1 = attachmentBuilder("sight_uh1");
        ItemEntry scope1_5x_srs = attachmentBuilder("sight_srs_02");
        ItemEntry scope1_5x2 = attachmentBuilder("sight_t2");
        ItemEntry scope1_5x = attachmentBuilder("sight_t1");
        ItemEntry scope1_3x = attachmentBuilder("sight_coyote");
        ItemEntry scope1_25x2_fast = attachmentBuilder("sight_fastfire_rifle");
        ItemEntry scope1_25x2_delta = attachmentBuilder("sight_deltapoint_rifle");
        ItemEntry scope1_25x2_acro = attachmentBuilder("sight_acro_rifle");
        ItemEntry scope1_25x2_pk06 = attachmentBuilder("sight_pk06_rifle");
        ItemEntry scope1_25x_fast = attachmentBuilder("sight_fastfire_pistol");
        ItemEntry scope1_25x_delta = attachmentBuilder("sight_deltapoint_pistol");
        ItemEntry scope1_25x_acro = attachmentBuilder("sight_acro_pistol");
        ItemEntry scope1_25x_pk06 = attachmentBuilder("sight_pk06_pistol");
        ItemEntry scope1_2x_sro = attachmentBuilder("sight_sro_dot");
        ItemEntry scope1_2x_rmr = attachmentBuilder("sight_rmr_dot");

        ItemEntry scope6xs = attachmentBuilder("scope_1873_6x");
        ItemEntry scope3_25xs = attachmentBuilder("scope_retro_2x");
        ItemEntry sight1_5xs = attachmentBuilder("sight_okp7");

        return new WeightEntry(Arrays.asList(
                // 5 8倍镜+
                new WeightedEntry(5, scope10x),
                // 15 6倍镜+
                new WeightedEntry(7, scope6_5x),
                new WeightedEntry(8, scope6_25x),
                // 30 3倍镜+
                new WeightedEntry(7, scope4_25x2),
                new WeightedEntry(9, scope4_25x),
                new WeightedEntry(14, scope3_25x),
                // 45 2倍镜
                new WeightedEntry(9, scope2_5x),
                new WeightedEntry(12, scope2x_552),
                new WeightedEntry(12, scope2x_exp3),
                new WeightedEntry(12, scope2x_uh1),
                // 60 红点
                new WeightedEntry(4, scope1_5x_srs),
                new WeightedEntry(4, scope1_5x2),
                new WeightedEntry(4, scope1_5x),
                new WeightedEntry(4, scope1_3x),
                new WeightedEntry(4, scope1_25x2_fast),
                new WeightedEntry(4, scope1_25x2_delta),
                new WeightedEntry(4, scope1_25x2_acro),
                new WeightedEntry(4, scope1_25x2_pk06),
                new WeightedEntry(5, scope1_25x_fast),
                new WeightedEntry(5, scope1_25x_delta),
                new WeightedEntry(5, scope1_25x_acro),
                new WeightedEntry(5, scope1_25x_pk06),
                new WeightedEntry(4, scope1_2x_sro),
                new WeightedEntry(4, scope1_2x_rmr),
                // 25 特殊
                new WeightedEntry(7, scope6xs),
                new WeightedEntry(14, scope3_25xs),
                new WeightedEntry(4, sight1_5xs)
        ));
    }

    private static ILootEntry rareScopeEntry() {
        ItemEntry scope25x = attachmentBuilder("scope_mk5hd");
        ItemEntry scope10x = attachmentBuilder("scope_standard_8x");
        ItemEntry scope6_5x = attachmentBuilder("scope_vudu");
        ItemEntry scope6_25x = attachmentBuilder("scope_lpvo_1_6");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, scope25x),
                new WeightedEntry(10, scope10x),
                new WeightedEntry(15, scope6_5x),
                new WeightedEntry(15, scope6_25x)
        ));
    }

    private static ILootEntry commonLaserEntry() {
        ItemEntry lopro = attachmentBuilder("laser_lopro");
        ItemEntry night = attachmentBuilder("laser_nightstick");
        ItemEntry compact = attachmentBuilder("laser_compact");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, lopro),
                new WeightedEntry(40, night),
                new WeightedEntry(40, compact)
        ));
    }

    private static ItemEntry ammoBuilder(String ammoName, int ammoCount) {
        return new ItemEntry("tacz:ammo", "{AmmoId:\"tacz:" + ammoName + "\"}", ammoCount);
    }

    private static ILootEntry commonAmmoEntry() {
        // 栓狙
        ItemEntry m24 = ammoBuilder("30_06", 15);
        ItemEntry spring = ammoBuilder("45_70", 5);
        // 连狙
        ItemEntry mk14 = ammoBuilder("308", 20);
        ItemEntry sks = ammoBuilder("762x39", 20);
        ItemEntry spr = ammoBuilder("556x45", 20);
        // 喷子
        ItemEntry _12g = ammoBuilder("12g", 10);
        // 步枪
        ItemEntry t1Rifle = ammoBuilder("308", 30);
        ItemEntry _762 = ammoBuilder("762x39", 30);
        ItemEntry _556 = ammoBuilder("556x45", 30);
        ItemEntry qbz = ammoBuilder("58x42", 30);
        // 机枪
        ItemEntry rpk = ammoBuilder("762x39", 40);
        ItemEntry m249 = ammoBuilder("556x45", 40);
        // 冲锋枪
        ItemEntry _45acp = ammoBuilder("45acp", 30);
        ItemEntry _9mm = ammoBuilder("9mm", 30);
        // 手枪
        ItemEntry deagle = ammoBuilder("50ae", 14);
        ItemEntry deagleG = ammoBuilder("357mag", 18);
        ItemEntry _45acp_pistol = ammoBuilder("45acp", 12);
        ItemEntry _9mm_pistol = ammoBuilder("9mm", 15);
        ItemEntry sawedOff = ammoBuilder("12g", 5);
        return new WeightEntry(Arrays.asList(
                // 5 栓狙
                new WeightedEntry(4, m24),
                new WeightedEntry(1, spring),
                // 10 连狙
                new WeightedEntry(2, mk14),
                new WeightedEntry(4, sks),
                new WeightedEntry(4, spr),
                // 15 喷子
                new WeightedEntry(15, _12g),
                // 30 步枪
                new WeightedEntry(1, m249),
                new WeightedEntry(1, rpk),
                new WeightedEntry(1, t1Rifle),
                new WeightedEntry(4, qbz),
                new WeightedEntry(10, _762),
                new WeightedEntry(13, _556),
                // 40 冲锋枪，手枪
                new WeightedEntry(4, deagle),
                new WeightedEntry(4, deagleG),
                new WeightedEntry(4, sawedOff),
                new WeightedEntry(6, _45acp),
                new WeightedEntry(6, _9mm),
                new WeightedEntry(10, _45acp_pistol),
                new WeightedEntry(10, _9mm_pistol)
        ));
    }

    private static ILootEntry rareAmmoEntry() {
        // 大狙
        ItemEntry t1SR = ammoBuilder("50bmg", 10);
        ItemEntry awm = ammoBuilder("338", 10);
        // 连狙
        ItemEntry mk14 = ammoBuilder("308", 20);
        // 机枪
        ItemEntry minigun = ammoBuilder("308", 40);
        // 步枪
        ItemEntry t1Rifle = ammoBuilder("308", 30);
        // 冲锋枪
        ItemEntry p90 = ammoBuilder("57x28", 50);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, t1SR),
                new WeightedEntry(10, awm),
                new WeightedEntry(10, p90),
                new WeightedEntry(3, minigun),
                new WeightedEntry(12, t1Rifle),
                new WeightedEntry(20, mk14)
        ));
    }
}
