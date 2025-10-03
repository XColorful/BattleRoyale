package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
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

import java.util.*;

public class BiomeEntry implements ILootEntry {
    private final boolean invert;
    private final List<String> biomeList;
    private final Set<ResourceKey<Biome>> biomes = new HashSet<>();
    private final ILootEntry entry;

    public BiomeEntry(boolean invert, List<String> biomeList,
                      ILootEntry entry) {
        this.invert = invert;
        this.biomeList = biomeList;
        for (String id : biomeList) {
            biomes.add(ResourceKey.create(Registries.BIOME, BattleRoyale.getMcRegistry().createResourceLocation(id)));
        }
        this.entry = entry;
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.INVERT, invert);
        jsonObject.add(LootEntryTag.FILTER, JsonUtils.writeStringListToJson(biomeList));
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }
}
