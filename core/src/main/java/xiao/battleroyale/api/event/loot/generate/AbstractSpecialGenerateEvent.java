package xiao.battleroyale.api.event.loot.generate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.loot.LootGenerator;

public abstract class AbstractSpecialGenerateEvent <T extends BlockEntity> extends AbstractGenerateEvent<T> {

    protected final String protocol;
    protected final @NotNull CompoundTag tag;

    public AbstractSpecialGenerateEvent(LootGenerator.LootContext lootContext, T target, String protocol, @NotNull CompoundTag tag) {
        super(lootContext, target);
        this.protocol = protocol;
        this.tag = tag;
    }

    public String getProtocol() {
        return protocol;
    }

    public @NotNull CompoundTag getTag() {
        return tag;
    }
}
