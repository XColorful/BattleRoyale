package xiao.battleroyale.api.event.loot.generate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.List;

public class CustomGenerateEventData<T extends BlockEntity> extends AbstractSpecialGenerateEventData<T> {

    public final List<ILootData> lootData;

    public CustomGenerateEventData(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull CompoundTag tag,
                                   List<ILootData> lootData) {
        super(lootContext, target, protocol, tag);
        this.lootData = lootData;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.CUSTOM_GENERATE_EVENT;
    }
}
