package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RandomEntry implements ILootEntry {
    private final double chance;
    private final ILootEntry entry;

    public RandomEntry(double chance, @Nullable ILootEntry entry) {
        if (chance < 0) {
            chance = 0;
        }
        this.chance = chance;
        this.entry = entry;
    }

    @Override
    public @NotNull List<ILootData> generateLootData(Supplier<Float> random) {
        if (random.get() < chance) {
            if (entry != null) {
                try {
                    return entry.generateLootData(random);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.warn("Failed to parse random entry");
                }
            } else {
                BattleRoyale.LOGGER.warn("RandomEntry missing entry member, skipped");
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_RANDOM;
    }

    public static RandomEntry fromJson(JsonObject jsonObject) {
        double chance = JsonUtils.getJsonDouble(jsonObject, LootEntryTag.CHANCE, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new RandomEntry(chance, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.CHANCE, this.chance);
        if (this.entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }
}