package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexEntry implements ILootEntry {
    public boolean invert;
    public String regexString;
    public Pattern pattern;
    public ILootEntry entry;

    public RegexEntry(boolean invert, String regexString, ILootEntry entry) {
        this.invert = invert;
        this.regexString = regexString;
        this.pattern = Pattern.compile(regexString);
        this.entry = entry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        if (entry != null) {
            if (target == null) {
                return Collections.emptyList();
            }

            try {
                CompoundTag nbt = target.saveWithFullMetadata();
                String nbtString = nbt.toString();
                Matcher matcher = pattern.matcher(nbtString);
                if (matcher.find() != invert) {
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
        return LootEntryTag.TYPE_REGEX;
    }

    @NotNull
    public static RegexEntry fromJson(JsonObject jsonObject) {
        boolean invert = JsonUtils.getJsonBool(jsonObject, LootEntryTag.INVERT, false);
        String regexString = JsonUtils.getJsonString(jsonObject, LootEntryTag.REGEX, "");
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new RegexEntry(invert, regexString, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.INVERT, invert);
        jsonObject.addProperty(LootEntryTag.REGEX, regexString);
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }
}
