package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.EffectFuncEntry;
import xiao.battleroyale.config.common.game.zone.zonefunc.InventoryFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.SquareEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.loot.type.EmptyEntry;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.LootEntryType;
import xiao.battleroyale.config.common.loot.type.MultiEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class ElytraAddon {

    private static final String DEFAULT_FILE_NAME = "example_elytra_addon.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        zoneConfigJson.add(generateLevitationEffect2());
        zoneConfigJson.add(generateElytraEquipment3());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static JsonObject generateLevitationEffect2() {
        EffectFuncEntry effectFuncEntry = new EffectFuncEntry.EffectFuncEntryBuilder(0, 20, 20, 0)
                .add("minecraft:levitation", 20, 1)
                .add("minecraft:glowing", 20, 1)
                .build();

        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(0, 1)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(2, "Levitation Effect", "#AAAAAA11",
                0, 200,
                effectFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }

    private static JsonObject generateElytraEquipment3() {
        ILootEntry lootEntry = new MultiEntry(Arrays.asList(
                new EmptyEntry(LootEntryType.ITEM.getName()),
                new EmptyEntry(LootEntryType.ITEM.getName()),
                new ItemEntry("minecraft:elytra", "{Damage:332}", 1),
                new ItemEntry("minecraft:iron_helmet", "", 1)
        ));
        InventoryFuncEntry inventoryFuncEntry = new InventoryFuncEntry(0, 0, 20, 0,
                false, false, 36, 39,
                lootEntry, -1);

        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(0, 0)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(0, 1)
                .addPreviousDimension(0, 0)
                .addDimensionScale(1.01);

        SquareEntry squareEntry = new SquareEntry(startEntry, endEntry, false);

        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(3, "Initial elytra equipment", "#BBBBBB22",
                80, 20,
                inventoryFuncEntry, squareEntry);

        return zoneConfig.toJson();
    }
}
