package xiao.battleroyale.config.common.loot.type.event;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.event.loot.generate.CustomGenerateEvent;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventEntry extends AbstractEventLootEntry {

    public EventEntry(String protocol, @Nullable JsonObject jsonTag) {
        super(protocol, jsonTag);
    }
    @Override public @NotNull EventEntry copy() {
        return new EventEntry(protocol, jsonTag.deepCopy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        List<ILootData> lootData = new ArrayList<>();
        if (EventPoster.postEvent(new CustomGenerateEvent<>(lootContext, target, protocol, jsonTag, lootData))) {
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
        JsonObject jsonTag = JsonUtils.getJsonObject(jsonObject, LootEntryTag.JSON_TAG, null);

        return new EventEntry(protocol, jsonTag);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        return jsonObject;
    }
}
