package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.loot.ILootEntry;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.*;

public class BiomeEntry extends AbstractLootEntry {
    public boolean invert;
    public final List<String> biomeList;
    public final Set<ResourceKey<Biome>> biomes = new HashSet<>();
    public ILootEntry entry;

    public BiomeEntry(boolean invert, List<String> biomeList,
                      ILootEntry entry) {
        this.invert = invert;
        this.biomeList = biomeList;
        for (String id : biomeList) {
            biomes.add(ResourceKey.create(Registries.BIOME, BattleRoyale.getMcRegistry().createResourceLocation(id)));
        }
        this.entry = entry;
    }
    @Override public @NotNull BiomeEntry copy() {
        return new BiomeEntry(invert, new ArrayList<>(biomeList), entry.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        if (entry != null) {
            try {
                boolean inBiome;
                if (target != null) {
                    Holder<Biome> biomeHolder = lootContext.serverLevel.getBiome(target.getBlockPos());
                    inBiome = biomeHolder.unwrapKey().isPresent() &&
                            biomes.contains(biomeHolder.unwrapKey().get());
                } else {
                    inBiome = false;
                }
                if (inBiome == invert) {
                    return entry.generateLootData(lootContext, target);
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_BIOME;
    }

    @NotNull
    public static BiomeEntry fromJson(JsonObject jsonObject) {
        boolean invert = JsonUtils.getJsonBool(jsonObject, LootEntryTag.INVERT, false);
        List<String> biomeList = JsonUtils.getJsonStringList(jsonObject, LootEntryTag.FILTER);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new BiomeEntry(invert, biomeList, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.INVERT, invert);
        jsonObject.add(LootEntryTag.FILTER, JsonUtils.writeStringListToJson(biomeList));
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }

    @Override
    public @Nullable JsonObject toLootTable() {
        /*
            期望生成的 JSON 样子（包裹 Item）：
            {
                "type": "minecraft:item",
                "name": "...",
                "conditions": [
                    {
                        "condition": "minecraft:location_check",
                        "predicate": { "biome": "minecraft:plains" }
                    }
                ]
            }

            期望生成的 JSON 样子（包裹 Pool/Repeat）：
            {
                "rolls": 1,
                "entries": [...],
                "conditions": [
                    {
                        "condition": "minecraft:location_check",
                        "predicate": { "biome": "minecraft:plains" }
                    }
                ]
            }
        */
        if (entry == null) return null;
        JsonObject json = entry.toLootTable();
        if (json == null) return null;

        // 如果 biomeList 为空，直接返回子项
        if (biomeList.isEmpty()) return json;

        // 构造 location_check 条件
        JsonObject locationCheck = new JsonObject();
        locationCheck.addProperty("condition", "minecraft:location_check");
        JsonObject predicate = new JsonObject();
        // 转换列表中的第一个群系 ID
        predicate.addProperty("biome", biomeList.get(0));
        locationCheck.add("predicate", predicate);

        // 处理反转逻辑
        JsonObject finalCondition;
        if (this.invert) {
            finalCondition = new JsonObject();
            finalCondition.addProperty("condition", "minecraft:inverted");
            finalCondition.add("term", locationCheck);
        } else {
            finalCondition = locationCheck;
        }

        // 无论 json 是 pool (有 rolls) 还是 entry (有 type)，原版都支持在根级放 conditions
        JsonArray conditions;
        if (json.has("conditions")) {
            conditions = json.getAsJsonArray("conditions");
        } else {
            conditions = new JsonArray();
            json.add("conditions", conditions);
        }

        conditions.add(finalCondition);

        return json;
    }
}
