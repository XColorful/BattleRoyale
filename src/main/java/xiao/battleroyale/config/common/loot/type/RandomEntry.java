package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RandomEntry implements ILootEntry {
    private final double chance;
    private final ILootEntry entry;

    public RandomEntry(double chance, ILootEntry entry) {
        if (chance < 0) {
            chance = 0;
        }
        this.chance = chance;
        this.entry = entry;
    }

    @Override
    public List<ILootData> generateLootData(Supplier<Float> random) {
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
        return "random";
    }

    public static RandomEntry fromJson(JsonObject jsonObject) {
        double chance = jsonObject.has("chance") ? jsonObject.getAsJsonPrimitive("chance").getAsDouble() : 0;
        ILootEntry entry = jsonObject.has("entry") ? JsonUtils.deserializeLootEntry(jsonObject.getAsJsonObject("entry")) : null;
        return new RandomEntry(chance, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty("chance", this.chance);
        if (this.entry != null) {
            jsonObject.add("entry", this.entry.toJson());
        }
        return jsonObject;
    }
}