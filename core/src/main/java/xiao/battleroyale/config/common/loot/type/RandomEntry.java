package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
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

import java.util.Collections;
import java.util.List;

public class RandomEntry extends AbstractLootEntry {
    public double chance;
    public ILootEntry entry;

    public RandomEntry(double chance, @Nullable ILootEntry entry) {
        if (chance < 0) {
            chance = 0;
        }
        this.chance = chance;
        this.entry = entry;
    }
    @Override public @NotNull RandomEntry copy() {
        return new RandomEntry(chance, entry.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        if (lootContext.random.get() < chance) {
            if (entry != null) {
                try {
                    return entry.generateLootData(lootContext, target);
                } catch (Exception e) {
                    parseErrorLog(e, target);
                }
            } else {
                entryErrorLog(target);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_RANDOM;
    }

    @NotNull
    public static RandomEntry fromJson(JsonObject jsonObject) {
        double chance = JsonUtils.getJsonDouble(jsonObject, LootEntryTag.CHANCE, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new RandomEntry(chance, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.CHANCE, this.chance);
        if (this.entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }

    @Override
    public JsonObject toLootTable() {
        /*
            {
                "type": "...",
                "conditions": [
                    {
                        "condition": "minecraft:random_chance",
                        "chance": 0.5
                    }
                ]
            }
        */
        JsonObject entryJson = entry != null ? entry.toLootTable() : null;
        if (entryJson == null) return null;

        // 构造 random_chance 条件
        JsonObject randomChance = new JsonObject();
        randomChance.addProperty("condition", "minecraft:random_chance");
        randomChance.addProperty("chance", this.chance);

        JsonArray conditions;

        // 如果子项已经有 conditions，需要合并
        if (entryJson.has("conditions")) {
            conditions = entryJson.getAsJsonArray("conditions");
        } else {
            conditions = new JsonArray();
            entryJson.add("conditions", conditions);
        }

        conditions.add(randomChance);

        return entryJson;
    }
}