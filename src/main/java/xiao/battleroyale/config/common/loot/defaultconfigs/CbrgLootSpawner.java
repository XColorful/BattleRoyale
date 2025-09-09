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
        MultiEntry minigun = gunSemiAmmoBuilder("minigun", "308", 48);
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
                new WeightedEntry(5, rareSMGEntry())
        ));
        return weaponTypeWeight;
    }

    // 全自动
    private static MultiEntry gunAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunName, namespace, ammoName, ammoCount, "AUTO");
    }

    private static MultiEntry gunBurstAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunBurstAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunBurstAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunName, namespace, ammoName, ammoCount, "BURST");
    }

    // 单发
    private static MultiEntry gunSemiAmmoBuilder(String gunName, String ammoName, int ammoCount) {
        return gunSemiAmmoBuilder(gunName, "tacz", ammoName, ammoCount);
    }
    private static MultiEntry gunSemiAmmoBuilder(String gunName, String namespace, String ammoName, int ammoCount) {
        return gunAmmoBuilderEmpty(gunName, namespace, ammoName, ammoCount, "SEMI");
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
    private static MultiEntry gunAmmoBuilderEmpty(String gunName, String namespace, String ammoName, int ammoCount, String fireMode) {
        String emptyAttachmentsNBT = "{" +
                "AttachmentEXTENDED_MAG: {id: \"minecraft:air\", Count:0b, tag: {struck:0b, glide:0b}}, " +
                "HasBulletInBarrel: 0b, " +
                "AttachmentSCOPE: {id: \"minecraft:air\", Count: 0b, tag: {struck: 0b, glide: 0b}}, " +
                "AttachmentMUZZLE: {id: \"minecraft:air\", Count: 0b, tag: {struck: 0b, glide: 0b}}, " +
                "AttachmentLASER: {id: \"minecraft:air\", Count: 0b, tag: {struck: 0b, glide: 0b}}, " +
                "GunFireMode: \"" + fireMode + "\", " +
                "GunCurrentAmmoCount: 0, " +
                "AttachmentGRIP: {id: \"minecraft:air\", Count: 0b, tag: {struck: 0b, glide: 0b}}, " +
                "AttachmentSTOCK: {id: \"minecraft:air\", Count: 0b, tag: {struck:0b, glide:0b}}, " +
                "GunId: \"tacz:" + gunName + "\"" +
                "}";

        return new MultiEntry(Arrays.asList(
                new ItemEntry("tacz:modern_kinetic_gun" , emptyAttachmentsNBT, 1),
                ammoBuilder(namespace, ammoName, ammoCount)
        ));
    }

    private static ILootEntry commonAREntry() {
        MultiEntry _811Ammo = gunAmmoBuilder("type_81", "cbrg", "ammo_762", 30);
        MultiEntry akmAmmo = gunAmmoBuilder("ak47", "cbrg", "ammo_762", 30);
        MultiEntry m16a4Ammo = gunBurstAmmoBuilder("m16a4", "cbrg", "ammo_556", 30);
        MultiEntry scarlAmmo = gunAmmoBuilder("scar_l", "cbrg", "ammo_556", 30);
        MultiEntry qbzAmmo = gunAmmoBuilder("qbz_95", "cbrg", "ammo_556", 30);
        MultiEntry m416Ammo = gunAmmoBuilder("hk416d", "cbrg", "ammo_556", 30);
        MultiEntry g36kAmmo = gunAmmoBuilder("g36k", "cbrg", "ammo_556", 30);
        MultiEntry m16a1Ammo = gunAmmoBuilder("m16a1", "cbrg", "ammo_556", 20);
        MultiEntry augAmmo = gunAmmoBuilder("aug", "cbrg", "ammo_556", 30);
        MultiEntry m4a1Ammo = gunAmmoBuilder("m4a1", "cbrg", "ammo_556", 30);
        MultiEntry rpk = gunAmmoBuilder("rpk", "cbrg", "ammo_762", 40);
        MultiEntry m249 = gunAmmoBuilder("m249", "cbrg", "ammo_556", 40);

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
        MultiEntry _811Ammo = gunAmmoBuilder("type_81", "cbrg", "ammo_762", 30);
        MultiEntry akmAmmo = gunAmmoBuilder("ak47", "cbrg", "ammo_762", 30);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, scarhAmmo),
                new WeightedEntry(5, g3Ammo),
                new WeightedEntry(10, _811Ammo),
                new WeightedEntry(10, akmAmmo)
        ));
    }

    private static ILootEntry commonSREntry() {
        MultiEntry m24Ammo = gunSemiAmmoBuilder("m700", "cbrg", "ammo_762", 5);
        MultiEntry springAmmo = gunSemiAmmoBuilder("springfield1873", "45_70", 5); // 这什么鸟枪只能装一发子弹
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, m24Ammo),
                new WeightedEntry(40, springAmmo)
        ));
    }

    private static ILootEntry rareSREntry() {
        MultiEntry m95Ammo = gunSemiAmmoBuilder("m95", "50bmg", 5);
        MultiEntry m107Ammo = gunSemiAmmoBuilder("m107", "50bmg", 10);
        MultiEntry awmAmmo = gunSemiAmmoBuilder("ai_awp", "338", 5);
        MultiEntry m24Ammo = gunSemiAmmoBuilder("m700", "cbrg", "ammo_762", 5);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, m95Ammo),
                new WeightedEntry(10, m107Ammo),
                new WeightedEntry(30, awmAmmo),
                new WeightedEntry(40, m24Ammo)
        ));
    }

    private static ILootEntry commonDMREntry() {
        MultiEntry sksAmmo = gunSemiAmmoBuilder("sks_tactical", "cbrg", "ammo_762", 10);
        MultiEntry sprAmmo = gunSemiAmmoBuilder("spr15hb", "cbrg", "ammo_556", 15);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(20, sksAmmo),
                new WeightedEntry(20, sprAmmo)
        ));
    }

    private static ILootEntry rareDMREntry() {
        MultiEntry mk14Ammo = gunSemiAmmoBuilder("mk14", "cbrg", "ammo_762", 10); // 默认不用全自动
        MultiEntry sksAmmo = gunSemiAmmoBuilder("sks_tactical", "cbrg", "ammo_762", 10);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, mk14Ammo),
                new WeightedEntry(10, sksAmmo)
        ));
    }

    private static ILootEntry commonShotgunEntry() {
        MultiEntry s1897Ammo = gunSemiAmmoBuilder("m870", "12g", 5);
        MultiEntry s12kAmmo = gunAmmoBuilder("aa12", "12g", 7);
        MultiEntry s686Ammo = gunSemiAmmoBuilder("db_long", "12g", 2);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(30, s1897Ammo),
                new WeightedEntry(40, s686Ammo),
                new WeightedEntry(30, s12kAmmo)
        ));
    }

    private static ILootEntry commonSMGEntry() {
        MultiEntry ump45Ammo = gunAmmoBuilder("ump45", "cbrg", "ammo_9mm", 25);
        MultiEntry vectorAmmo = gunAmmoBuilder("vector45", "cbrg", "ammo_9mm", 20);
        MultiEntry uziAmmo = gunAmmoBuilder("uzi", "cbrg", "ammo_9mm", 20);
        MultiEntry mp5Ammo = gunAmmoBuilder("hk_mp5a5", "cbrg", "ammo_9mm", 30);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(20, ump45Ammo),
                new WeightedEntry(15, vectorAmmo),
                new WeightedEntry(15, uziAmmo),
                new WeightedEntry(10, mp5Ammo)
        ));
    }

    private static ILootEntry rareSMGEntry() {
        MultiEntry p90Ammo = gunAmmoBuilder("p90", "57x28", 50);
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(5, p90Ammo)
        ));
    }

    private static ILootEntry commonPistolEntry() {
        MultiEntry sawedAmmo = gunSemiAmmoBuilder("db_short", "12g", 2); // 默认不用连发
        MultiEntry deagleAmmo = gunSemiAmmoBuilder("deagle", "cbrg", "ammo_45acp", 7);
        MultiEntry deagleGAmmo = gunSemiAmmoBuilder("deagle_golden", "cbrg", "ammo_45acp", 9);
        MultiEntry m1911Ammo = gunSemiAmmoBuilder("m1911", "cbrg", "ammo_45acp", 7);
        MultiEntry p320Ammo = gunSemiAmmoBuilder("p320", "cbrg", "ammo_45acp", 12);
        MultiEntry b93rAmmo = gunBurstAmmoBuilder("b93r", "cbrg", "ammo_45acp", 20);
        MultiEntry glockAmmo = gunSemiAmmoBuilder("glock_17", "cbrg", "ammo_45acp", 17);
        MultiEntry cz75Ammo = gunAmmoBuilder("cz75", "cbrg", "ammo_45acp", 16);
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

    // 补偿+消烟
    private static ILootEntry commonCompensatorEntry() {
        ItemEntry compensator_rifle = attachmentBuilder("cbrg", "compensator_rifle");
        ItemEntry flash_hider_rifle = attachmentBuilder("cbrg", "flash_hider_rifle");

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

        ItemEntry scope6xs = attachmentBuilder("scope_1873_6x");

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
                new WeightedEntry(20, canted_sight),
                // 25 特殊
                new WeightedEntry(25, scope6xs)
        ));
    }

    private static ILootEntry rareScopeEntry() {
        ItemEntry _15x_scope = attachmentBuilder("cbrg", "15x_scope");
        ItemEntry _8x_scope = attachmentBuilder("cbrg", "8x_scope");
        ItemEntry _6x_scope = attachmentBuilder("cbrg", "6x_scope");
        ItemEntry _4x_scope = attachmentBuilder("cbrg", "4x_scope");
        return new WeightEntry(Arrays.asList(
                new WeightedEntry(10, _15x_scope),
                new WeightedEntry(40, _8x_scope),
                new WeightedEntry(30, _6x_scope),
                new WeightedEntry(20, _4x_scope)
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
        return ammoBuilder("tacz", ammoName, ammoCount);
    }
    private static ItemEntry ammoBuilder(String namespace, String ammoName, int ammoCount) {
        return new ItemEntry("tacz:ammo", String.format("{AmmoId:\"%s:%s\"}", namespace, ammoName), ammoCount);
    }

    private static ILootEntry commonAmmoEntry() {
        // 栓狙
        ItemEntry m24 = ammoBuilder("30_06", 15);
        ItemEntry spring = ammoBuilder("45_70", 5);
        // 连狙
        ItemEntry mk14 = ammoBuilder("cbrg", "ammo_762", 20);
        ItemEntry sks = ammoBuilder("cbrg", "ammo_762", 20);
        ItemEntry spr = ammoBuilder("cbrg", "ammo_556", 20);
        // 喷子
        ItemEntry _12g = ammoBuilder("12g", 10);
        // 步枪
        ItemEntry t1Rifle = ammoBuilder("308", 30);
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
                new WeightedEntry(12, _9mm),
                new WeightedEntry(28, _45acp),
                new WeightedEntry(4, sawedOff)
        ));
    }

    private static ILootEntry rareAmmoEntry() {
        // 大狙
        ItemEntry t1SR = ammoBuilder("50bmg", 10);
        ItemEntry awm = ammoBuilder("338", 10);
        // 连狙
        ItemEntry mk14 = ammoBuilder("cbrg", "ammo_762", 20);
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
