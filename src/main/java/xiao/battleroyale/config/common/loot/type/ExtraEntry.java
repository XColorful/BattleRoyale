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

public class ExtraEntry implements ILootEntry {
    private final boolean countEmpty;
    private final boolean keepCheck;
    private final ILootEntry checkEntry;
    private final ILootEntry extraEntry;

    public ExtraEntry(boolean countEmpty, boolean keepCheck,
                      @Nullable ILootEntry checkEntry, @Nullable ILootEntry extraEntry) {
        this.countEmpty = countEmpty;
        this.keepCheck = keepCheck;
        this.checkEntry = checkEntry;
        this.extraEntry = extraEntry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>(); // 防止emptyList不能修改 (UnsupportedOperationException)
        if (checkEntry != null) {
            try {
                // 判断是否非空
                List<ILootData> checkData = checkEntry.generateLootData(lootContext, target);
                if (checkData.isEmpty() ||
                        (!countEmpty && checkData.stream().allMatch(ILootData::isEmpty))) {
                    return lootData;
                }
                // 生成物资刷新
                if (keepCheck) {
                    lootData.addAll(checkData);
                }
                if (extraEntry != null) {
                    lootData.addAll(extraEntry.generateLootData(lootContext, target));
                } else {
                    BattleRoyale.LOGGER.warn("ExtraEntry missing extraEntry member");
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_EXTRA;
    }

    @NotNull
    public static ExtraEntry fromJson(JsonObject jsonObject) {
        boolean countEmpty = JsonUtils.getJsonBool(jsonObject, LootEntryTag.COUNT_EMPTY, false);
        boolean keepCheck = JsonUtils.getJsonBool(jsonObject, LootEntryTag.KEEP_CHECK, false);
        JsonObject checkEntryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.CHECK, null);
        ILootEntry checkEntry = LootConfig.deserializeLootEntry(checkEntryObject);
        JsonObject extraEntryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.EXTRA, null);
        ILootEntry extraEntry = LootConfig.deserializeLootEntry(extraEntryObject);
        return new ExtraEntry(countEmpty, keepCheck,
                checkEntry, extraEntry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.COUNT_EMPTY, countEmpty);
        jsonObject.addProperty(LootEntryTag.KEEP_CHECK, keepCheck);
        if (checkEntry != null) {
            jsonObject.add(LootEntryTag.CHECK, checkEntry.toJson());
        }
        if (extraEntry != null) {
            jsonObject.add(LootEntryTag.EXTRA, extraEntry.toJson());
        }
        return jsonObject;
    }
}
