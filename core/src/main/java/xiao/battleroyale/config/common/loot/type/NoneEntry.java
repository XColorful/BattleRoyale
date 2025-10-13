package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NoneEntry implements ILootEntry {

    @Override public @NotNull NoneEntry copy() {
        return new NoneEntry();
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_NONE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        return jsonObject;
    }

    @NotNull
    public static NoneEntry fromJson(JsonObject jsonObject) {
        return new NoneEntry();
    }
}