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

public class RepeatEntry implements ILootEntry {
    private final int min;
    private final int max;
    private final ILootEntry entry;

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

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, T target) {
        int repeats = min + (int) (lootContext.random.get() * (max - min + 1));
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            try {
                for (int i = 0; i < repeats; i++) {
                    lootData.addAll(entry.generateLootData(lootContext, target));
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse repeat entry, skipped at {}", target.getBlockPos(), e);
            }
        } else {
            BattleRoyale.LOGGER.warn("RepeatEntry missing entry member, skipped at {}", target.getBlockPos());
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
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