package xiao.battleroyale.compat.forge.event.loot.generate;

import net.minecraft.world.level.block.entity.BlockEntity;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.compat.forge.event.loot.AbstractLootEvent;

public abstract class AbstractGenerateEvent<T extends BlockEntity> extends AbstractLootEvent {

    protected final LootGenerator.LootContext lootContext;
    protected final T target;

    public AbstractGenerateEvent(LootGenerator.LootContext lootContext, T target) {
        this.lootContext = lootContext;
        this.target = target;
    }

    public LootGenerator.LootContext getLootContext() {
        return lootContext;
    }

    public T getTarget() {
        return target;
    }
}
