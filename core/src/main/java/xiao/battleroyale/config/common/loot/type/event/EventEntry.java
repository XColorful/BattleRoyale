package xiao.battleroyale.config.common.loot.type.event;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.loot.generate.CustomGenerateEvent;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventEntry extends AbstractEventLootEntry {

    public EventEntry(String protocol, @Nullable CompoundTag tag) {
        super(protocol, tag);
    }
    @Override public @NotNull EventEntry copy() {
        return new EventEntry(protocol, tag.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (EventPoster.postEvent(new CustomGenerateEvent<>(lootContext, target, protocol, tag, lootData))) {
            return lootData;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_EVENT;
    }

    @NotNull
    public static EventEntry fromJson(JsonObject jsonObject) {
        String protocol = JsonUtils.getJsonString(jsonObject, LootEntryTag.PROTOCOL, "");
        CompoundTag tag = JsonUtils.getJsonTag(jsonObject, LootEntryTag.TAG, null);

        return new EventEntry(protocol, tag);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        return jsonObject;
    }
}
