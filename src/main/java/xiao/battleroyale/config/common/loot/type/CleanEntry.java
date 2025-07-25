package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class CleanEntry implements ILootEntry {
    private final ILootEntry entry;

    public CleanEntry(ILootEntry entry) {
        this.entry = entry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            entry.generateLootData(lootContext, target).stream()
                    .filter(data -> !data.isEmpty())
                    .forEach(lootData::add);
        } else {
            BattleRoyale.LOGGER.warn("CleanEntry missing entry member, skipped");
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_CLEAN;
    }

    @NotNull
    public static CleanEntry fromJson(JsonObject jsonObject) {
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new CleanEntry(entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        return jsonObject;
    }
}
