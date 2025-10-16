package xiao.battleroyale.compat.neoforge.event.loot.generate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.loot.generate.CustomGenerateEventData;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.common.loot.LootGenerator;

import java.util.List;

public class CustomGenerateEvent<T extends BlockEntity> extends AbstractSpecialGenerateEvent<T> {

    protected final List<ILootData> lootData;

    public CustomGenerateEvent(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull CompoundTag tag,
                               List<ILootData> lootData) {
        super(lootContext, target, protocol, tag);
        this.lootData = lootData;
    }

    public List<ILootData> getLootData() {
        return lootData;
    }

    public static CustomGenerateEvent<?> createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof CustomGenerateEventData<?> data)) {
            throw new RuntimeException("Expected CustomGenerateEventData but received: " + customEventData.getClass().getName());
        }
        return new CustomGenerateEvent<>(data.lootContext, data.target, data.protocol, data.tag,
                data.lootData);
    }
}
