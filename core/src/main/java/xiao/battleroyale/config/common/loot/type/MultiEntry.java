package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiEntry implements ILootEntry {
    public final @NotNull List<ILootEntry> entries;

    public MultiEntry(@NotNull List<ILootEntry> entries) {
        this.entries = entries;
    }
    @Override public @NotNull MultiEntry copy() {
        List<ILootEntry> entriesCopy = new ArrayList<>(entries.size());
        for (ILootEntry entry : entries) {
            entriesCopy.add(entry.copy());
        }
        return new MultiEntry(entriesCopy);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (!entries.isEmpty()) {
            try {
                for (ILootEntry entry : entries) {
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
        return LootEntryTag.TYPE_MULTI;
    }

    public static List<ILootEntry> getEntries(JsonObject jsonObject) {
        List<ILootEntry> entries = new ArrayList<>();
        if (jsonObject.has(LootEntryTag.ENTRIES)) {
            JsonArray entriesArray = JsonUtils.getJsonArray(jsonObject, LootEntryTag.ENTRIES, null);
            if (entriesArray != null) {
                for (JsonElement element : entriesArray) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject entryObject = element.getAsJsonObject();
                    ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        }
        return entries;
    }

    @NotNull
    public static MultiEntry fromJson(JsonObject jsonObject) {
        return new MultiEntry(getEntries(jsonObject));
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add(LootEntryTag.ENTRIES, entriesArray);
        return jsonObject;
    }
}