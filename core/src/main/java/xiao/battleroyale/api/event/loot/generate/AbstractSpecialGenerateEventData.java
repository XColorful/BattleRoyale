package xiao.battleroyale.api.event.loot.generate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.loot.LootGenerator;

public abstract class AbstractSpecialGenerateEventData<T extends BlockEntity> extends AbstractGenerateEventData<T> {

    public final String protocol;
    public final @NotNull CompoundTag tag;

    public AbstractSpecialGenerateEventData(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull CompoundTag tag) {
        super(lootContext, target);
        this.protocol = protocol;
        this.tag = tag;
    }
}
