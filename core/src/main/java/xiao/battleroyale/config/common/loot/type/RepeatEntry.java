package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class RepeatEntry extends AbstractLootEntry {
    public int min;
    public int max;
    public ILootEntry entry;

    public RepeatEntry(int min, int max, @Nullable ILootEntry entry) {
        if (min < 0) {
            min = 0;
        }
        this.min = min;
        if (max < min) {
            max = min;
        }
        this.max = max;
        this.entry = entry;
    }
    @Override public @NotNull RepeatEntry copy() {
        return new RepeatEntry(min, max, entry.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        int repeats = min + (int) (lootContext.random.get() * (max - min + 1));
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            try {
                for (int i = 0; i < repeats; i++) {
                    lootData.addAll(entry.generateLootData(lootContext, target));
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_REPEAT;
    }

    @NotNull
    public static RepeatEntry fromJson(JsonObject jsonObject) {
        int min = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MIN, 0);
        int max = JsonUtils.getJsonInt(jsonObject, LootEntryTag.MAX, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new RepeatEntry(min, max, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        if (min >= 0) {
            jsonObject.addProperty(LootEntryTag.MIN, this.min);
        }
        if (max >= 0) {
            jsonObject.addProperty(LootEntryTag.MAX, this.max);
        }
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }

    @Override
    public JsonObject toLootTable() {
        /*

            {
                "rolls": { "min": 1, "max": 5 },
                "entries": [
                    { "type": "minecraft:item", "name": "..." }
                ]
            }
        */
        JsonObject pool = new JsonObject();
        JsonObject rolls = new JsonObject();
        rolls.addProperty("min", this.min);
        rolls.addProperty("max", this.max);
        pool.add("rolls", rolls);
        JsonArray entriesArray = new JsonArray();
        if (entry != null) {
            JsonObject result = entry.toLootTable();
            if (result != null) {
                // root
                if (result.has("pools")) {
                    JsonArray pools = result.getAsJsonArray("pools");
                    for (JsonElement poolElement : pools) {
                        JsonObject poolObj = poolElement.getAsJsonObject();
                        JsonArray subEntries = poolObj.getAsJsonArray("entries");
                        for (JsonElement e : subEntries) {
                            entriesArray.add(e);
                        }
                    }
                }
                // pool
                else if (result.has("rolls")) {
                    JsonArray subEntries = result.getAsJsonArray("entries");
                    for (JsonElement e : subEntries) {
                        entriesArray.add(e);
                    }
                }
                // entry
                else if (result.has("type")) {
                    entriesArray.add(result);
                }
            }
        }
        pool.add("entries", entriesArray);
        return pool;
    }
}