package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class AbortEntry extends AbstractLootEntry {
    public int lootDistance;
    public int slotDistance;
    public boolean countEmpty;
    public final @NotNull List<ILootEntry> entries;

    public AbortEntry(MultiEntry multiEntry) {
        this(multiEntry.copy().entries);
    }
    public AbortEntry(@NotNull List<ILootEntry> entries) {
        this(1, 1, false, entries);
    }
    public AbortEntry(int lootDistance, int slotDistance, boolean countEmpty,
                      @NotNull List<ILootEntry> entries) {
        this.lootDistance = lootDistance;
        this.slotDistance = slotDistance;
        this.countEmpty = countEmpty;
        this.entries = entries;
    }
    @Override public @NotNull AbortEntry copy() {
        List<ILootEntry> entriesCopy = new ArrayList<>(entries.size());
        for (ILootEntry entry : entries) {
            entriesCopy.add(entry.copy());
        }
        return new AbortEntry(lootDistance, slotDistance, countEmpty,
                entriesCopy);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (!entries.isEmpty()) {
            try {
                int currentLootDistance = 0;
                int currentSlotDistance = 0;
                for (ILootEntry entry : entries) {
                    List<ILootData> currentLoot = entry.generateLootData(lootContext, target);
                    if (!currentLoot.isEmpty()) {
                        int currentSlotStep = 0;
                        if (countEmpty) {
                            currentSlotStep = currentLoot.size();
                        } else {
                            for (ILootData data : currentLoot) {
                                if (!data.isEmpty()) currentSlotStep++;
                            }
                        }
                        if (countEmpty || currentSlotStep > 0) currentLootDistance++;
                    }
                    lootData.addAll(currentLoot);
                    if (currentLootDistance >= lootDistance || currentSlotDistance >= slotDistance) {
                        break; // 急停
                    }
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
        return LootEntryTag.TYPE_ABORT;
    }

    @NotNull
    public static AbortEntry fromJson(JsonObject jsonObject) {
        int lootDistance = JsonUtils.getJsonInt(jsonObject, LootEntryTag.LOOT_DISTANCE, 1);
        int slotDistance = JsonUtils.getJsonInt(jsonObject, LootEntryTag.SLOT_DISTANCE, 1);
        boolean countEmpty = JsonUtils.getJsonBoolean(jsonObject, LootEntryTag.COUNT_EMPTY, false);
        List<ILootEntry> entries = MultiEntry.getEntries(jsonObject);
        return new AbortEntry(lootDistance, slotDistance, countEmpty,
                entries);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.LOOT_DISTANCE, lootDistance);
        jsonObject.addProperty(LootEntryTag.SLOT_DISTANCE, slotDistance);
        jsonObject.addProperty(LootEntryTag.COUNT_EMPTY, countEmpty);
        JsonArray entriesArray = new JsonArray();
        for (ILootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        jsonObject.add(LootEntryTag.ENTRIES, entriesArray);
        return jsonObject;
    }
}
