package xiao.battleroyale.common.server.utility.lootconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.ILootEntry;

public class LootTableGenerator {

    public static @Nullable JsonObject toLootTableJson(@NotNull ILootEntry lootEntry) {
        @Nullable JsonObject lootTableJson = lootEntry.toLootTable();
        if (lootTableJson == null) return null;

        // 1. 已经是根结构 (如 MultiEntry)，直接返回
        if (lootTableJson.has("pools")) {
            return lootTableJson;
        }

        // 2. Pool 结构 (如 RepeatEntry 或 WeightEntry)
        // 需要包装成 {"pools": [ ... ]}
        if (lootTableJson.has("rolls")) {
            JsonObject root = new JsonObject();
            JsonArray pools = new JsonArray();
            pools.add(lootTableJson);
            root.add("pools", pools);
            return root;
        }

        // 3. Entry 结构 (比如最外层就是一个简单的 ItemEntry)
        // 需要包装成 {"pools": [{"rolls": 1, "entries": [ ... ]}]}
        if (lootTableJson.has("type")) {
            JsonObject root = new JsonObject();
            JsonArray pools = new JsonArray();

            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            JsonArray entries = new JsonArray();
            entries.add(lootTableJson);

            pool.add("entries", entries);
            pools.add(pool);
            root.add("pools", pools);
            return root;
        }

        // 其他自定义逻辑（既没有 pools 也没有 rolls/type）
        return lootTableJson;
    }
}
