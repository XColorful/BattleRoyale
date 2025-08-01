package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
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
    private final boolean invert;
    private final String regexString;
    private final Pattern pattern;
    private final ILootEntry entry;

    public RegexEntry(boolean invert, String regexString, ILootEntry entry) {
        this.invert = invert;
        this.regexString = regexString;
        this.pattern = Pattern.compile(regexString);
        this.entry = entry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, T target) {
        if (entry != null) {
            try {
                CompoundTag nbt = target.saveWithFullMetadata();
                String nbtString = nbt.toString();
                Matcher matcher = pattern.matcher(nbtString);
                if (matcher.find() != invert) {
                    return entry.generateLootData(lootContext, target);
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse regex entry, skipped at {}", target.getBlockPos(), e);
            }
        } else {
            BattleRoyale.LOGGER.warn("RegexEntry missing entry member, skipped at {}", target.getBlockPos());
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
