package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
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
}