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

import java.util.Collections;
import java.util.List;

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
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, T target) {
        if (lootContext.random.get() < chance) {
            if (entry != null) {
                try {
                    return entry.generateLootData(lootContext, target);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.warn("Failed to parse random entry, skipped at {}", target.getBlockPos(), e);
                }
            } else {
                BattleRoyale.LOGGER.warn("RandomEntry missing entry member, skipped at {}", target.getBlockPos());
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.CHANCE, this.chance);
        if (this.entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }
}