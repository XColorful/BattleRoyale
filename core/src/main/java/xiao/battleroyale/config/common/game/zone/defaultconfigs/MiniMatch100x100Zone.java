package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.EquipmentLevel;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.InventoryFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.loot.type.EmptyEntry;
import xiao.battleroyale.config.common.loot.type.LootEntryType;
import xiao.battleroyale.config.common.loot.type.MultiEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg5340x5340Casual.generateBorderCircle;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual.addPhase;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class MiniMatch100x100Zone {

    private static final String DEFAULT_FILE_NAME = "example_100x100_minimatch.json";
    private static final String DEATH_MATCH_FILE_NAME = "example_100x100_deathmatch.json";

    private static final int INIT_ZONE_DELAY = 20 * 30; // 30秒

    private static final int ZONE9_MOVE_DELAY = 20 * 10;
    private static final int ZONE9_MOVE_TIME = 20 * 160;
    private static final int ZONE9_TIME = ZONE9_MOVE_DELAY + ZONE9_MOVE_TIME;
    private static final double ZONE9_SHRINK_RANGE = 0.5;
    private static final double ZONE9_SHRINK_SCALE = 0.001;
    private static final float ZONE9_DAMAGE = 18F / 5F;

    public static final int GAME_TIME = 99999; // 直接写死，无限时长

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        add100x100Zone(zoneConfigJson, true);
        addInitialItem(zoneConfigJson);
        addInitialEquipment(zoneConfigJson);
        addPhase9(zoneConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }
    public static void generateDeathMatchConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        add100x100Zone(zoneConfigJson, false);
        addInitialItem(zoneConfigJson);
        addInitialEquipment(zoneConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEATH_MATCH_FILE_NAME).toString(), zoneConfigJson);
    }

    private static void add100x100Zone(JsonArray zoneConfigJson, boolean borderDamage) {
        generateBorderCircle(zoneConfigJson, 100 / 2F, GAME_TIME, 0, false, 5, borderDamage);
        zoneConfigJson.add(ElytraAddon.generateLevitationGlowingEffect2(false, true));
    }

    private static void addInitialItem(JsonArray zoneConfigJson) {
        int itemLootId = 0;
        InventoryFuncEntry inventoryFuncEntry = new InventoryFuncEntry(0, 0, 20, 0,
                false, false, InventoryIndex.HOTBAR_START, InventoryIndex.INVENTORY_END,
                null, itemLootId);

        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 1)
                .addDimensionScale(1.01);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        int preZoneDelayId = 0;
        int zoneDelay = 20 * 3; // 开局3秒后刷东西
        ZoneConfig zoneConfig = new ZoneConfig(88, "Initial hotbar & inventory item", "#FFAA0000",
                preZoneDelayId, zoneDelay, 19,
                inventoryFuncEntry, squareEntry);

        zoneConfigJson.add(zoneConfig.toJson());
    }

    private static void addInitialEquipment(JsonArray zoneConfigJson) {
        ILootEntry lootEntry = new MultiEntry(Arrays.asList(
                new EmptyEntry(LootEntryType.ITEM.getName()),
                new EmptyEntry(LootEntryType.ITEM.getName()),
                EquipmentLevel.equipment(EquipmentLevel.DIAMOND, EquipmentLevel.CHESTPLATE),
                EquipmentLevel.equipment(EquipmentLevel.IRON, EquipmentLevel.HELMET)
        ));
        InventoryFuncEntry inventoryFuncEntry = new InventoryFuncEntry(0, 0, 20, 0,
                false, false, InventoryIndex.ARMOR_START, InventoryIndex.ARMOR_END,
                lootEntry, -1);

        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 1)
                .addDimensionScale(1.01);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        int preZoneDelayId = 0;
        int zoneDelay = 20 * 3; // 开局3秒后刷东西
        ZoneConfig zoneConfig = new ZoneConfig(89, "Initial equipment", "#FFAA0000",
                preZoneDelayId, zoneDelay, 19,
                inventoryFuncEntry, squareEntry);

        zoneConfigJson.add(zoneConfig.toJson());
    }

    private static void addPhase9(JsonArray zoneConfigJson) {
        addPhase(zoneConfigJson, 9, ZONE9_SHRINK_RANGE, ZONE9_SHRINK_SCALE, INIT_ZONE_DELAY, ZONE9_TIME + GAME_TIME, // 无限时长
                ZONE9_MOVE_DELAY, ZONE9_MOVE_TIME, ZONE9_DAMAGE, 9);
    }
}
