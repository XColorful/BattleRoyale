package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.util.JsonUtils;

import java.util.*;

public class StructureEntry implements ILootEntry {
    private final boolean invert;
    private final List<String> structureList;
    private final Set<ResourceKey<Structure>> structures = new HashSet<>();
    private final ILootEntry entry;

    public StructureEntry(boolean invert, List<String> structureList,
                          ILootEntry entry) {
        this.invert = invert;
        this.structureList = structureList;
        for (String id : structureList) {
            structures.add(ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(id)));
        }
        this.entry = entry;
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, T target) {
        if (entry != null) {
            try {
                BlockPos pos = target.getBlockPos();
                boolean inStructure = false;

                StructureManager structureManager = lootContext.serverLevel.structureManager();

                for (ResourceKey<Structure> structureKey : structures) {
                    StructureStart structureStart = structureManager.getStructureWithPieceAt(pos, structureKey);
                    if (structureStart != StructureStart.INVALID_START) {
                        inStructure = true;
                        break;
                    }
                }

                if (inStructure == invert) {
                    return entry.generateLootData(lootContext, target);
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse structure entry, skipped at {}", target.getBlockPos(), e);
            }
        } else {
            BattleRoyale.LOGGER.warn("StructureEntry missing entry member, skipped at {}", target.getBlockPos());
        }
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_STRUCTURE;
    }

    @NotNull
    public static StructureEntry fromJson(JsonObject jsonObject) {
        boolean invert = JsonUtils.getJsonBool(jsonObject, LootEntryTag.INVERT, false);
        List<String> structureList = JsonUtils.getJsonStringList(jsonObject, LootEntryTag.FILTER);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfig.deserializeLootEntry(entryObject);
        return new StructureEntry(invert, structureList, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.INVERT, invert);
        jsonObject.add(LootEntryTag.FILTER, JsonUtils.writeStringListToJson(structureList));
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }
}