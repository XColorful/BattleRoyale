package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.common.game.zone.tickable.InventoryFunc;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.util.JsonUtils;

import javax.annotation.Nullable;

public class InventoryFuncEntry extends AbstractFuncEntry {

    public boolean skipNonEmptySlot;
    public boolean dropBeforeReplace;
    public int firstSlotIndex;
    public int lastSlotIndex;
    public @Nullable ILootEntry lootEntry;
    public int lootSpawnerLootId;

    public InventoryFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                              boolean skipNonEmptySlot, boolean dropBeforeReplace, int firstSlotIndex, int lastSlotIndex,
                              @Nullable ILootEntry lootEntry, int lootSpawnerLootId) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.skipNonEmptySlot = skipNonEmptySlot;
        this.dropBeforeReplace = dropBeforeReplace;
        this.firstSlotIndex = firstSlotIndex;
        this.lastSlotIndex = lastSlotIndex;
        this.lootEntry = lootEntry;
        this.lootSpawnerLootId = lootSpawnerLootId;
    }
    @Override public @NotNull InventoryFuncEntry copy() {
        return new InventoryFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                skipNonEmptySlot, dropBeforeReplace, firstSlotIndex, lastSlotIndex,
                lootEntry != null ? lootEntry.copy() : null, lootSpawnerLootId);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.INVENTORY;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new InventoryFunc(moveDelay, moveTime, tickFreq, tickOffset,
                skipNonEmptySlot, dropBeforeReplace, firstSlotIndex, lastSlotIndex,
                lootEntry != null ? lootEntry.copy() : null, lootSpawnerLootId);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.SKIP_NON_EMPTY_SLOT, skipNonEmptySlot);
        jsonObject.addProperty(ZoneFuncTag.DROP_BEFORE_REPLACE, dropBeforeReplace);
        jsonObject.addProperty(ZoneFuncTag.FIRST_SLOT_INDEX, firstSlotIndex);
        jsonObject.addProperty(ZoneFuncTag.LAST_SLOT_INDEX, lastSlotIndex);
        jsonObject.add(ZoneFuncTag.INVENTORY_LOOT_ENTRY, lootEntry != null ? lootEntry.toJson() : new JsonObject());
        jsonObject.addProperty(ZoneFuncTag.INVENTORY_LOOT_SPAWNER_LOOT_ID, lootSpawnerLootId);

        return jsonObject;
    }

    public static InventoryFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        boolean skipNonEmptySlot = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.SKIP_NON_EMPTY_SLOT, false);
        boolean dropBeforeReplace = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.DROP_BEFORE_REPLACE, false);
        int firstSlotIndex = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FIRST_SLOT_INDEX, InventoryIndex.HOTBAR_START);
        int lastSlotIndex = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.LAST_SLOT_INDEX, InventoryIndex.OFFHAND_END);
        ILootEntry lootEntry = LootConfigManager.LootConfig.deserializeLootEntry(JsonUtils.getJsonObject(jsonObject, ZoneFuncTag.INVENTORY_LOOT_ENTRY, null));
        int lootSpawnerLootId = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.INVENTORY_LOOT_SPAWNER_LOOT_ID, 0);

        return new InventoryFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                skipNonEmptySlot, dropBeforeReplace, firstSlotIndex, lastSlotIndex,
                lootEntry, lootSpawnerLootId);
    }
}
