package xiao.battleroyale.api.event.loot.generate;

import net.minecraft.world.level.block.entity.BlockEntity;
import xiao.battleroyale.api.event.loot.AbstractLootEventData;
import xiao.battleroyale.common.loot.LootGenerator;

public abstract class AbstractGenerateEventData<T extends BlockEntity> extends AbstractLootEventData {

    public final LootGenerator.LootContext lootContext;
    public final T target;

    public AbstractGenerateEventData(LootGenerator.LootContext lootContext, T target) {
        this.lootContext = lootContext;
        this.target = target;
    }
}
