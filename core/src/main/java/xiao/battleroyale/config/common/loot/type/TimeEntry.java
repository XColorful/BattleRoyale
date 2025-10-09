package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TimeEntry implements ILootEntry {
    public int start;
    public int end;
    public ILootEntry entry;

    public TimeEntry(int start, int end, @Nullable ILootEntry entry) {
        this.start = start;
        this.end = end;
        this.entry = entry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        int gameTime = GameManager.get().getGameTime();
        if (entry != null) {
            if (start <= gameTime && gameTime <= end) {
                try {
                    return entry.generateLootData(lootContext, target);
                } catch (Exception e) {
                    parseErrorLog(e, target);
                }
            }
        } else {
            entryErrorLog(target);
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_TIME;
    }

    @NotNull
    public static TimeEntry fromJson(JsonObject jsonObject) {
        int start = JsonUtils.getJsonInt(jsonObject, LootEntryTag.START, 0);
        int end = JsonUtils.getJsonInt(jsonObject, LootEntryTag.END, 0);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new TimeEntry(start, end, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.START, this.start);
        jsonObject.addProperty(LootEntryTag.END, this.end);
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, this.entry.toJson());
        }
        return jsonObject;
    }
}