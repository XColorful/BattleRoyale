package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.Collections;
import java.util.List;

public class NoneEntry extends AbstractLootEntry {

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
        JsonObject jsonObject = super.toJson();
        return jsonObject;
    }

    @NotNull
    public static NoneEntry fromJson(JsonObject jsonObject) {
        return new NoneEntry();
    }
}