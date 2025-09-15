package xiao.battleroyale.config.common.loot.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.EquipmentLevel;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.type.*;
import xiao.battleroyale.config.common.loot.type.WeightEntry.WeightedEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.api.minecraft.EquipmentLevel.*;
import static xiao.battleroyale.config.common.loot.LootConfigTypeEnum.LOOT_SPAWNER;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CbrgLootSpawner {

    private static final String DEFAULT_FILE_NAME = "example_tacz_cbrg.json";
    private static final String EXTRA_FILE_NAME = "example_tacz_cbrg_extra.json";

    public static void generateDefaultConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateTaczCommonLoot());
        lootSpawnerConfigsJson.add(generateTaczRareLoot());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(LOOT_SPAWNER), LootConfigManager.LOOT_SPAWNER_CONFIG_SUB_PATH, DEFAULT_FILE_NAME).toString(), lootSpawnerConfigsJson);
    }

    public static void generateExtraConfigs() {
        JsonArray lootSpawnerConfigsJson = new JsonArray();
        lootSpawnerConfigsJson.add(generateTaczExtraLoot());
        lootSpawnerConfigsJson.add(generateTaczRareLoot());
        writeJsonToFile(Paths.get(LootConfigManager.get().getConfigPath(LOOT_SPAWNER), LootConfigManager.LOOT_SPAWNER_CONFIG_SUB_PATH, EXTRA_FILE_NAME).toString(), lootSpawnerConfigsJson);
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
                new WeightedEntry(30, commonWeaponEntry()),
                new WeightedEntry(22, commonAttachmentEntry()),
                new WeightedEntry(8, commonAmmoEntry()),
                new WeightedEntry(22, commomEquipmentEntry()),
                new WeightedEntry(8, commonHealEntry()),
                new WeightedEntry(10, commonToolEntry())
        ));
        RepeatEntry repeatEntry = new RepeatEntry(1, 3, itemTypeWeight);

        MultiEntry multiEntry = new MultiEntry(Arrays.asList(
                repeatEntry,
                minecraftItemEntry(),
                specialItemEntry()
        ));

        LootConfig lootConfig = new LootConfig(0, "Widespread common loot", "#FFFFFFAA",
                multiEntry);

        return lootConfig.toJson();
    }

    private static JsonObject generateTaczExtraLoot() {
        WeightEntry itemTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(30, commonWeaponEntry()),
                new WeightedEntry(22, commonAttachmentEntry()),
                new WeightedEntry(8, commonAmmoEntry()),
                new WeightedEntry(22, commomEquipmentEntry()),
                new WeightedEntry(8, commonHealEntry()),
                new WeightedEntry(10, commonToolEntry())
        ));
        RepeatEntry repeatEntry = new RepeatEntry(3, 6, itemTypeWeight);

        MultiEntry multiEntry = new MultiEntry(Arrays.asList(
                repeatEntry,
                minecraftItemEntry(),
                specialItemEntry(),
                generateExtraRareLootEntry()
        ));

        LootConfig lootConfig = new LootConfig(0, "Widespread common loot (extra rare)", "#FFFFFFAA",
                multiEntry);

        return lootConfig.toJson();
    }
    private static ILootEntry generateExtraRareLootEntry() {
        return new ExtraEntry(false, true,
                new RandomEntry(0.01, generateTaczRareLootEntry()),
                new MessageEntry(false, true, "Rare loot generated！", "#FF0000"));
    }

    private static ILootEntry generateTaczRareLootEntry() {
        return new MultiEntry(Arrays.asList(
                rareWeaponEntry(),
                rareAttachmentEntry(),
                rareAmmoEntry(),
                rareEquipmentEntry()
        ));
    }
    private static JsonObject generateTaczRareLoot() {
        LootConfig lootConfig = new LootConfig(1, "Rare high-tier loots", "#FFFFFFAA",
                new RepeatEntry(2, 2, generateTaczRareLootEntry()));

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
        MultiEntry minigun = gunSemiAmmoBuilder("minigun", "cbrg", "ammo_762", 48);
        // 火箭筒
        MultiEntry rpg = gunSemiAmmoBuilder("rpg7", "rpg_rocker", 1);
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
                new WeightedEntry(23, commonAREntry()),
                new WeightedEntry(11, commonSREntry()),
                new WeightedEntry(12, commonDMREntry()),
                new WeightedEntry(13, commonShotgunEntry()),
                new WeightedEntry(22, commonSMGEntry()),
                new WeightedEntry(8, commonPistolEntry()),
                new WeightedEntry(11, commonMeleeEntry())
        ));

        return weaponTypeWeight;
    }

    private static ILootEntry rareWeaponEntry() {
        WeightEntry weaponTypeWeight = new WeightEntry(Arrays.asList(
                new WeightedEntry(15, rareAREntry()),
                new WeightedEntry(5, rareSREntry()),
                new WeightedEntry(10, rareDMREntry()),
                new WeightedEntry(0, rareShotgunEntry()),
                new WeightedEntry(5, rareSMGEntry())
        ));
        return weaponTypeWeight;
    }

    // 全自动
    private static MultiEntry gunAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty("tacz", gunName, namespace, ammoName, ammoCount, "AUTO");
    }
    private static MultiEntry gunAmmoBuilder(String gunNamespace, String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunNamespace, gunName, namespace, ammoName, ammoCount, "AUTO");
    }

    private static MultiEntry gunBurstAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunBurstAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunBurstAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty("tacz", gunName, namespace, ammoName, ammoCount, "BURST");
    }
    private static MultiEntry gunBurstAmmoBuilder(String gunNamespace, String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunNamespace, gunName, namespace, ammoName, ammoCount, "BURST");
    }

    // 单发
    private static MultiEntry gunSemiAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunSemiAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunSemiAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty("tacz", gunName, namespace, ammoName, ammoCount, "SEMI");
    }
    private static MultiEntry gunSemiAmmoBuilder(String gunNamespace, String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunNamespace, gunName, namespace, ammoName, ammoCount, "SEMI");
    }

    /**
     * 枪+子弹
     */
    private static MultiEntry gunAmmoBuilder(String gunName, String ammoName, int ammoCount, String fireMode) {
        return new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun", "{GunId:\"tacz:" + gunName + "\",GunFireMode:\"" + fireMode + "\"}", 1),
                ammoBuilder(ammoName, ammoCount)
        ));
    }
    // 强制写入空配件的NBT标签字符串
    private static MultiEntry gunAmmoBuilderEmpty(String gunNamespace, String gunName, String namespace, String ammoName, int ammoCount, String fireMode) {
        String emptyAttachmentsNBT = "{" +
                "GunFireMode: \"" + fireMode + "\", " +
                "GunId: \"" + gunNamespace + ":" + gunName + "\"" +
                "}";

        return new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun" , emptyAttachmentsNBT, 1),
                ammoBuilder(namespace, ammoName, ammoCount)
        ));
    }

    private static ILootEntry commonAREntry() {
        MultiEntry m249Ammo = gunAmmoBuilder("m249", "cbrg", "ammo_556", 40);
        MultiEntry dp28Ammo = gunAmmoBuilder("rpk", "cbrg", "ammo_762", 40);

        MultiEntry beryl_m762Ammo = gunAmmoBuilder("cbrg", "beryl_m762", "cbrg", "ammo_762", 30);

        MultiEntry qbzAmmo = gunAmmoBuilder("qbz_95", "cbrg", "ammo_556", 30);
        MultiEntry augAmmo = gunAmmoBuilder("aug", "cbrg", "ammo_556", 30);

        MultiEntry k2Ammo = gunAmmoBuilder("cib", "k2", "cbrg", "ammo_556", 30);
        MultiEntry akmAmmo = gunAmmoBuilder("ak47", "cbrg", "ammo_762", 30);
        MultiEntry ace32Ammo = gunAmmoBuilder("cib", "galilace32", "cbrg", "ammo_762", 30);
        MultiEntry scarlAmmo = gunAmmoBuilder("scar_l", "cbrg", "ammo_556", 30);

        MultiEntry g36cAmmo = gunAmmoBuilder("g36k", "cbrg", "ammo_556", 30);
        MultiEntry m416Ammo = gunAmmoBuilder("hk416d", "cbrg", "ammo_556", 30);
        MultiEntry m16a4Ammo = gunBurstAmmoBuilder("m16a4", "cbrg", "ammo_556", 30);
        // Mk47 Mutant

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(2, m249Ammo),
                new WeightedEntry(5, dp28Ammo),
                new WeightedEntry(10, beryl_m762Ammo),
                new WeightedEntry(10, qbzAmmo),
                new WeightedEntry(15, augAmmo),
                new WeightedEntry(15, k2Ammo),
                new WeightedEntry(20, akmAmmo),
                new WeightedEntry(20, ace32Ammo),
                new WeightedEntry(25, scarlAmmo),
                new WeightedEntry(25, g36cAmmo),
                new WeightedEntry(30, m416Ammo),
                new WeightedEntry(35, m16a4Ammo)
        ));
    }

    private static ILootEntry rareAREntry() {
        MultiEntry grozaAmmo = gunAmmoBuilder("cbrg", "groza", "cbrg", "ammo_762", 20);
        // FAMAS
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, grozaAmmo)
        ));
    }

    private static ILootEntry commonSREntry() {
        MultiEntry m24Ammo = gunSemiAmmoBuilder("m700", "cbrg", "ammo_762", 15);
        MultiEntry mosin_nagantAmmo = gunSemiAmmoBuilder("bf1", "man_m95", "cbrg", "ammo_762", 15);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, m24Ammo),
                new WeightedEntry(40, mosin_nagantAmmo)
        ));
    }

    private static ILootEntry rareSREntry() {
        String lynx_amrNBT = "{" +
                "HasBulletInBarrel: 0b, " +
                "AttachmentSCOPE: {id: \"tacz:attachment\", Count: 1b, tag: {AttachmentId:\"cbrg:6x_scope\"}}, " +
                "GunFireMode: \"" + "SEMI" + "\", " +
                "GunCurrentAmmoCount: 10, " +
                "GunId: \"tacz:" + "m107" + "\"" +
                "}";
        MultiEntry lynx_amrAmmo = new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun", lynx_amrNBT, 1)
        ));

        String awmNBT = "{" +
                "GunFireMode: \"" + "SEMI" + "\", " +
                "GunId: \"" + "tacz" + ":" + "ai_awp" + "\"" +
                "}";
        MultiEntry awmAmmo = new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun", awmNBT, 1),
                ammoBuilder("338", 10),
                ammoBuilder("338", 10)
        ));

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, lynx_amrAmmo),
                new WeightedEntry(30, awmAmmo)
        ));
    }

    private static ILootEntry commonDMREntry() {
        MultiEntry dragunovAmmo = gunSemiAmmoBuilder("cib","svd", "cbrg", "ammo_762", 10);
        MultiEntry slrAmmo = gunSemiAmmoBuilder("cbrg","slr", "cbrg", "ammo_762", 10);
        
        MultiEntry sksAmmo = gunSemiAmmoBuilder("sks_tactical", "cbrg", "ammo_762", 10);
        // VSS

        // Mini14
        MultiEntry mk12Ammo = gunSemiAmmoBuilder("spr15hb", "cbrg", "ammo_556", 15);
        
        MultiEntry qbuAmmo = gunSemiAmmoBuilder("cib", "qbu88", "cbrg", "ammo_556", 15);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(10, dragunovAmmo),
                new WeightedEntry(10, slrAmmo),
                new WeightedEntry(15, sksAmmo),
                new WeightedEntry(20, mk12Ammo),
                new WeightedEntry(20, qbuAmmo)
        ));
    }

    private static ILootEntry rareDMREntry() {
        MultiEntry mk14Ammo = gunSemiAmmoBuilder("mk14", "cbrg", "ammo_762", 10); // 默认不用全自动
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, mk14Ammo)
        ));
    }

    private static ILootEntry commonShotgunEntry() {
        MultiEntry o12Ammo = gunSemiAmmoBuilder("cib", "origin12", "cbrg", "ammo_12g", 5);
        
        MultiEntry s12kAmmo = gunAmmoBuilder("aa12", "cbrg", "ammo_12g", 7);
        MultiEntry s1897Ammo = gunSemiAmmoBuilder("m870", "cbrg", "ammo_12g", 5);
        
        MultiEntry s686Ammo = gunSemiAmmoBuilder("cib", "686", "cbrg", "ammo_12g", 2);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, o12Ammo),
                new WeightedEntry(25, s12kAmmo),
                new WeightedEntry(30, s1897Ammo),
                new WeightedEntry(40, s686Ammo)
        ));
    }

    private static ILootEntry rareShotgunEntry() {
        // DBS

        return new WeightEntry(Arrays.asList(

        ));
    }

    private static ILootEntry commonSMGEntry() {
        MultiEntry pp19_bizonAmmo = gunAmmoBuilder("cib", "pp19", "cbrg", "ammo_9mm", 25);
        // Tommy Gun
        // JS9

        MultiEntry ump45Ammo = gunAmmoBuilder("ump45", "cbrg", "ammo_9mm", 25);
        MultiEntry vectorAmmo = gunAmmoBuilder("vector45", "cbrg", "ammo_9mm", 20);

        MultiEntry mp5kAmmo = gunAmmoBuilder("hk_mp5a5", "cbrg", "ammo_9mm", 30);
        // MP9

        MultiEntry uziAmmo = gunAmmoBuilder("uzi", "cbrg", "ammo_9mm", 20);

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(15, pp19_bizonAmmo),
                new WeightedEntry(20, ump45Ammo),
                new WeightedEntry(20, vectorAmmo),
                new WeightedEntry(25, mp5kAmmo),
                new WeightedEntry(25, uziAmmo)
        ));
    }

    private static ILootEntry rareSMGEntry() {
        MultiEntry p90Ammo = gunAmmoBuilder("origin", "p90", "cbrg", "ammo_57mm", 50);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, p90Ammo)
        ));
    }

    private static ILootEntry commonPistolEntry() {
        MultiEntry p18cAmmo = gunAmmoBuilder("cib", "g18c", "cbrg", "ammo_9mm", 15);
        MultiEntry skorpionAmmo = gunAmmoBuilder("cz75", "cbrg", "ammo_45acp", 15);

        MultiEntry deagleAmmo = gunSemiAmmoBuilder("deagle", "cbrg", "ammo_45acp", 7);
        MultiEntry sawedAmmo = gunSemiAmmoBuilder("db_short", "cbrg", "ammo_12g", 2); // 默认不用连发

        // R1895
        // R45

        MultiEntry p1911Ammo = gunSemiAmmoBuilder("m1911", "cbrg", "ammo_45acp", 7);
        MultiEntry p92Ammo = gunSemiAmmoBuilder("p320", "cbrg", "ammo_45acp", 12);

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, p18cAmmo),
                new WeightedEntry(5, skorpionAmmo),
                new WeightedEntry(20, deagleAmmo),
                new WeightedEntry(25, sawedAmmo),
                new WeightedEntry(30, p1911Ammo),
                new WeightedEntry(35, p92Ammo)
        ));
    }

    private static ILootEntry commonMeleeEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, new ItemEntry("lrtactical:melee", "{MeleeWeaponId:\"lrtactical:baseball_bat\"}", 1)),
                new WeightedEntry(10, new ItemEntry("lrtactical:melee", "{MeleeWeaponId:\"lrtactical:karambit\"}", 1)),
                new WeightedEntry(15, new ItemEntry("lrtactical:melee", "{MeleeWeaponId:\"lrtactical:dagger\"}", 1)),
                new WeightedEntry(70, commonMcMeleeEntry())
        ));
    }

    private static ILootEntry commonMcMeleeEntry() {
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
                new WeightedEntry(33, commonScopeEntry())
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

    // 补偿+消烟
    private static ILootEntry commonCompensatorEntry() {
        ItemEntry compensator_rifle = attachmentBuilder("cbrg", "compensator_rifle");
        ItemEntry flash_hider_rifle = attachmentBuilder("cbrg", "flash_hider_rifle");
        // Muzzle Brake (AR, DMR, O12, S12K)

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(40, compensator_rifle),
                new WeightedEntry(60, flash_hider_rifle)
        ));
    }

    private static ILootEntry commonSilencerEntry() {
        ItemEntry suppressor_rifle = attachmentBuilder("cbrg", "suppressor_rifle");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(100, suppressor_rifle)
        ));
    }

    private static ILootEntry commonMagazineEntry() {
        ItemEntry srMag2 = attachmentBuilder("sniper_extended_mag_2");
        ItemEntry srMag1 = attachmentBuilder("sniper_extended_mag_1");
        ItemEntry quickdraw_mag_rifle = attachmentBuilder("cbrg", "quickdraw_mag_rifle");
        ItemEntry extended_mag_rifle = attachmentBuilder("cbrg", "extended_mag_rifle");
        ItemEntry extended_quickdraw_mag_rifle = attachmentBuilder("cbrg", "extended_quickdraw_mag_rifle");
        ItemEntry light3 = attachmentBuilder("light_extended_mag_3");
        ItemEntry light2 = attachmentBuilder("light_extended_mag_2");
        ItemEntry light1 = attachmentBuilder("light_extended_mag_1");
        return new WeightEntry(Arrays.asList(
                // 5
                new WeightedEntry(1.5, srMag2),
                new WeightedEntry(3.5, srMag1),
                // 15
                new WeightedEntry(4, extended_quickdraw_mag_rifle),
                new WeightedEntry(6, extended_mag_rifle),
                new WeightedEntry(8, quickdraw_mag_rifle),
                // 25
                new WeightedEntry(5, light3),
                new WeightedEntry(7, light2),
                new WeightedEntry(10, light1)
        ));
    }

    private static ILootEntry rareMagazineEntry() {
        ItemEntry srMag3 = attachmentBuilder("sniper_extended_mag_3");
        ItemEntry extended_quickdraw_mag_rifle = attachmentBuilder("cbrg", "extended_quickdraw_mag_rifle");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, srMag3),
                new WeightedEntry(10, extended_quickdraw_mag_rifle)
        ));
    }

    private static ILootEntry commonStockEntry() {
        ItemEntry cheek_pad = attachmentBuilder("cbrg", "cheek_pad");
        // Bullet Loops (SG, SR, Win94)

        ItemEntry tactical_stock = attachmentBuilder("stock_tactical_ar");
        ItemEntry heavy_stock = attachmentBuilder("stock_hk_slim_line");

        // Folding Stock (Skorpion, Micro UZI, MP9)

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, cheek_pad),
                new WeightedEntry(15, heavy_stock),
                new WeightedEntry(15, tactical_stock)
        ));
    }

    private static ILootEntry commonGripEntry() {
        ItemEntry angled_foregrip = attachmentBuilder("cbrg", "angled_foregrip");
        ItemEntry halfgrip = attachmentBuilder("cbrg", "halfgrip");
        ItemEntry laser_sight = attachmentBuilder("cbrg", "laser_sight");
        ItemEntry lightweight_grip = attachmentBuilder("cbrg", "lightweight_grip");
        ItemEntry thumbgrip = attachmentBuilder("cbrg", "thumbgrip");
        ItemEntry vertical_foregrip = attachmentBuilder("cbrg", "vertical_foregrip");

        return new WeightEntry(Arrays.asList(
                new WeightedEntry(15, angled_foregrip),
                new WeightedEntry(15, halfgrip),
                new WeightedEntry(10, laser_sight),
                new WeightedEntry(15, lightweight_grip),
                new WeightedEntry(15, thumbgrip),
                new WeightedEntry(15, vertical_foregrip)
        ));
    }

    private static ItemEntry attachmentBuilder(String attachmentName) {
        return attachmentBuilder("tacz", attachmentName);
    }

    private static ItemEntry attachmentBuilder(String namespace, String attachmentName) {
        return new ItemEntry("tacz:attachment", String.format("{AttachmentId:\"%s:%s\"}", namespace, attachmentName), 1);
    }

    private static ILootEntry commonScopeEntry() {
        ItemEntry canted_sight = attachmentBuilder("cbrg", "canted_sight");
        ItemEntry red_dot = attachmentBuilder("cbrg", "red_dot");
        ItemEntry holographic_sight = attachmentBuilder("cbrg", "holographic_sight");
        ItemEntry _2x_scope = attachmentBuilder("cbrg", "2x_scope");
        ItemEntry _3x_scope = attachmentBuilder("cbrg", "3x_scope");
        ItemEntry _4x_scope = attachmentBuilder("cbrg", "4x_scope");
        ItemEntry _6x_scope = attachmentBuilder("cbrg", "6x_scope");
        ItemEntry _8x_scope = attachmentBuilder("cbrg", "8x_scope");

        return new WeightEntry(Arrays.asList(
                // 5 8倍镜+
                new WeightedEntry(5, _8x_scope),
                // 15 6倍镜+
                new WeightedEntry(15, _6x_scope),
                // 30 3倍镜+
                new WeightedEntry(15, _4x_scope),
                new WeightedEntry(15, _3x_scope),
                // 45 2倍镜
                new WeightedEntry(45, _2x_scope),
                // 60 红点
                new WeightedEntry(20, red_dot),
                new WeightedEntry(20, holographic_sight),
                new WeightedEntry(20, canted_sight)
        ));
    }

    private static ILootEntry rareScopeEntry() {
        ItemEntry _15x_scope = attachmentBuilder("cbrg", "15x_scope");
        ItemEntry _8x_scope = attachmentBuilder("cbrg", "8x_scope");
        ItemEntry _6x_scope = attachmentBuilder("cbrg", "6x_scope");
        ItemEntry _4x_scope = attachmentBuilder("cbrg", "4x_scope");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, _15x_scope),
                new WeightedEntry(40, _8x_scope),
                new WeightedEntry(30, _6x_scope),
                new WeightedEntry(10, _4x_scope)
        ));
    }

    private static ItemEntry ammoBuilder(String ammoName, int ammoCount) {
        return ammoBuilder("tacz", ammoName, ammoCount);
    }
    private static ItemEntry ammoBuilder(String namespace, String ammoName, int ammoCount) {
        return new ItemEntry("tacz:ammo", String.format("{AmmoId:\"%s:%s\"}", namespace, ammoName), ammoCount);
    }

    private static ILootEntry commonAmmoEntry() {
        // 栓狙
        ItemEntry m24 = ammoBuilder("cbrg", "ammo_762", 15);
        ItemEntry spring = ammoBuilder("45_70", 5);
        // 连狙
        ItemEntry mk14 = ammoBuilder("cbrg", "ammo_762", 20);
        ItemEntry sks = ammoBuilder("cbrg", "ammo_762", 20);
        ItemEntry spr = ammoBuilder("cbrg", "ammo_556", 20);
        // 喷子
        ItemEntry _12g = ammoBuilder("cbrg", "ammo_12g", 10);
        // 步枪
        ItemEntry t1Rifle = ammoBuilder("cbrg", "ammo_762", 30);
        ItemEntry _762 = ammoBuilder("cbrg", "ammo_762", 30);
        ItemEntry _556 = ammoBuilder("cbrg", "ammo_556", 30);
        ItemEntry qbz = ammoBuilder("cbrg", "ammo_556", 30);
        // 机枪
        ItemEntry rpk = ammoBuilder("cbrg", "ammo_762", 40);
        ItemEntry m249 = ammoBuilder("cbrg", "ammo_556", 40);
        // 冲锋枪
        ItemEntry _9mm = ammoBuilder("cbrg", "ammo_9mm", 30);
        // 手枪
        ItemEntry _45acp = ammoBuilder("cbrg", "ammo_45acp", 12);
        ItemEntry sawedOff = ammoBuilder("cbrg", "ammo_12g", 5);
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
                new WeightedEntry(12, _9mm),
                new WeightedEntry(28, _45acp),
                new WeightedEntry(4, sawedOff)
        ));
    }

    private static ILootEntry rareAmmoEntry() {
        // AWM
        ItemEntry awm = ammoBuilder("338", 10);
        // P90
        ItemEntry p90 = ammoBuilder("cbrg", "ammo_57mm", 50);
        // Mk14, Groza
        ItemEntry _762 = ammoBuilder("cbrg", "ammo_762", 30);
        // FAMAS
        ItemEntry _556 = ammoBuilder("cbrg", "ammo_556", 36);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(10, awm),
                new WeightedEntry(10, p90),
                new WeightedEntry(25, _762),
                new WeightedEntry(30, _556)
        ));
    }

    /**
     * 钻石
     * 锁链
     * 皮革
     */
    private static ILootEntry commomEquipmentEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(45, commonVestEntry()),
                new WeightedEntry(30, commonLeggingsEntry()),
                new WeightedEntry(25, commonHelmetEntry())
        ));
    }

    private static ILootEntry rareEquipmentEntry() {
        return new MultiEntry(Arrays.asList(
                rareHelmetEntry(),
                rareVestEntry(),
                rareLeggingsEntry()
        ));
    }

    private static ILootEntry commonHelmetEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(55, EquipmentLevel.equipment(CHAINMAIL, HELMET, 10, 3)),
                new WeightedEntry(45, EquipmentLevel.equipment(IRON, HELMET, 15, 5))
        ));
    }

    private static ILootEntry rareHelmetEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(70, EquipmentLevel.equipment(IRON, HELMET, 15, 5)),
                new WeightedEntry(30, EquipmentLevel.equipment(NETHERITE, HELMET, 20, 8))
        ));
    }

    private static ILootEntry commonVestEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(55, EquipmentLevel.equipment(CHAINMAIL, CHESTPLATE, 15, 1)),
                new WeightedEntry(40, EquipmentLevel.equipment(IRON, CHESTPLATE, 20, 3)),
                new WeightedEntry(5, EquipmentLevel.equipment(DIAMOND, CHESTPLATE, 25, 5))
        ));
    }

    private static ILootEntry rareVestEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(60, EquipmentLevel.equipment(IRON, CHESTPLATE, 20, 3)),
                new WeightedEntry(40, EquipmentLevel.equipment(DIAMOND, CHESTPLATE, 25, 5))
        ));
    }

    private static ILootEntry commonLeggingsEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(65, EquipmentLevel.equipment(CHAINMAIL, LEGGINGS, 13, 1)),
                new WeightedEntry(35, EquipmentLevel.equipment(IRON, LEGGINGS, 18, 3))
        ));
    }

    private static ILootEntry rareLeggingsEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(55, EquipmentLevel.equipment(IRON, LEGGINGS, 18, 3)),
                new WeightedEntry(45, EquipmentLevel.equipment(DIAMOND, LEGGINGS, 23, 5))
        ));
    }

    private static ILootEntry commonHealEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(28, new ItemEntry("minecraft:potion", "{Potion:\"minecraft:empty\",display:{Name:'{\"text\":\"Bandage\",\"color\":\"white\",\"italic\":false}'},CustomPotionEffects:[{Id:10,Amplifier:0,Duration:100,ShowParticles:0b,Ambient:0b}]}", 1)),
                new WeightedEntry(16, new ItemEntry("minecraft:potion", "{Potion:\"minecraft:empty\",display:{Name:'{\"text\":\"First Aid Kit\",\"color\":\"red\",\"italic\":false}'},CustomPotionEffects:[{Id:10,Amplifier:3,Duration:90,ShowParticles:0b,Ambient:0b}]}", 1)),
                new WeightedEntry(2, new ItemEntry("minecraft:potion", "{Potion:\"minecraft:empty\",display:{Name:'{\"text\":\"Med Kit\",\"color\":\"blue\",\"italic\":false}'},CustomPotionEffects:[{Id:10,Amplifier:3,Duration:120,ShowParticles:0b,Ambient:0b}]}", 1))
                // new WeightedEntry(27, new EmptyEntry("item")),
                // new WeightedEntry(24, new EmptyEntry("item")),
                // new WeightedEntry(3, new EmptyEntry("item"))
        ));
    }

    private static ILootEntry commonToolEntry() {
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(16, new ItemEntry("lrtactical:throwable", "{ThrowableId:\"lrtactical:m67\"}", 1)),
                new WeightedEntry(26, new ItemEntry("lrtactical:throwable", "{ThrowableId:\"lrtactical:smoke_grenade\"}", 1)),
                new WeightedEntry(18, new ItemEntry("lrtactical:throwable", "{ThrowableId:\"lrtactical:molotov\"}", 1)),
                new WeightedEntry(22, new ItemEntry("lrtactical:throwable", "{ThrowableId:\"lrtactical:flash_grenade\"}", 1)),
                new WeightedEntry(3, new ItemEntry("grapplemod:grapplinghook", "{Damage:495,custom:{motor:1b,throwspeed:3d,maxlen:4}}", 1)),
                new WeightedEntry(5, new ItemEntry("vc_gliders:paraglider_wood", "{Damage:25}", 1))
                // new WeightedEntry(10, new EmptyEntry("item"))
        ));
    }
}
