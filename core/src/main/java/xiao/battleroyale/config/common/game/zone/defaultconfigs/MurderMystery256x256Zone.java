package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.InventoryFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.CircleEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual.generateBorder;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class MurderMystery256x256Zone {

    private static final String DEFAULT_FILE_NAME = "example_256x256_murdermystery.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        add256x256Zone(zoneConfigJson);
        addSurvivorItem(zoneConfigJson);
        addDetectiveItem(zoneConfigJson);
        addMurdererItem(zoneConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    private static void add256x256Zone(JsonArray zoneConfigJson) {
        generateBorder(zoneConfigJson, (float) 256 / 2, 99999, false, 10);
    }
    public static int SURVIVOR_ITEM_LOOT = 7;
    private static void addSurvivorItem(JsonArray zoneConfigJson) {
        addRoleItem(zoneConfigJson, SURVIVOR_ITEM_LOOT, "Survivor");
    }
    public static int DETECTIVE_ITEM_LOOT = 8;
    private static void addDetectiveItem(JsonArray zoneConfigJson) {
        addRoleItem(zoneConfigJson, DETECTIVE_ITEM_LOOT, "Detective");
    }
    public static int MURDERER_ITEM_LOOT = 9;
    private static void addMurdererItem(JsonArray zoneConfigJson) {
        addRoleItem(zoneConfigJson, MURDERER_ITEM_LOOT, "Murderer");
    }

    private static void addRoleItem(JsonArray zoneConfigJson, int itemLootId, String role) {
        InventoryFuncEntry inventoryFuncEntry = new InventoryFuncEntry(0, 0, 20, 0,
                true, false, InventoryIndex.HOTBAR_START, InventoryIndex.INVENTORY_END,
                null, itemLootId);
        StartEntry startEntry = new StartEntry()
                .addFixedDimension(Vec3.ZERO);
        EndEntry endEntry = new EndEntry()
                .addFixedDimension(Vec3.ZERO);
        CircleEntry circleEntry = new CircleEntry(startEntry, endEntry, false);
        int preZoneDelayId = 0;
        ZoneConfigManager.ZoneConfig zoneConfig = new ZoneConfigManager.ZoneConfig(itemLootId, role + " loot " + itemLootId, "#FFFFFF00",
                preZoneDelayId, 0, 20,
                inventoryFuncEntry, circleEntry);

        zoneConfigJson.add(zoneConfig.toJson());
    }
}
