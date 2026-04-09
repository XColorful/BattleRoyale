package xiao.battleroyale.api.event.loot.generate;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.event.EventDispatcher;

import java.util.List;

public class CustomGenerateEvent <T extends BlockEntity> extends AbstractSpecialGenerateEvent<T> {

    protected final List<ILootData> lootData;

    public CustomGenerateEvent(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull JsonObject jsonTag,
                               List<ILootData> lootData) {
        super(lootContext, target, protocol, jsonTag);
        this.lootData = lootData;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.CUSTOM_GENERATE_EVENT;
    }

    public List<ILootData> getLootData() {
        return lootData;
    }

    @Override public String getTextName() {
        return "CBR CustomGenerateEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(CustomGenerateEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
