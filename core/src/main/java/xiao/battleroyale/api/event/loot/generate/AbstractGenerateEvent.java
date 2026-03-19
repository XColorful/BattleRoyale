package xiao.battleroyale.api.event.loot.generate;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.loot.AbstractLootEvent;
import xiao.battleroyale.common.loot.LootGenerator;

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

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                target.getBlockPos().getCenter(),
                Vec2.ZERO,
                lootContext.serverLevel,
                4,
                this.getTextName(),
                this.getDisplayName(),
                lootContext.serverLevel.getServer(),
                null
        );
    }
    public abstract String getTextName();
    public abstract Component getDisplayName();
}
