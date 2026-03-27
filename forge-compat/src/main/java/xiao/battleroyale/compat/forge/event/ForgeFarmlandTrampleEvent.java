package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IFarmlandTrampleEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeFarmlandTrampleEvent extends ForgeEvent implements IFarmlandTrampleEvent {

    protected BlockEvent.FarmlandTrampleEvent farmlandTrampleEvent;

    public ForgeFarmlandTrampleEvent(Event event) {
        super(event);
        if (event instanceof BlockEvent.FarmlandTrampleEvent eventIn) {
            this.farmlandTrampleEvent = eventIn;
        } else {
            throw new RuntimeException("Expected FarmlandTrampleEvent but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.FARMLAND_TRAMPLE_EVENT;
    }

    @Override
    public LevelAccessor getLevelAccessor() {
        return farmlandTrampleEvent.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return farmlandTrampleEvent.getPos();
    }

    @Override
    public BlockState getBlockState() {
        return farmlandTrampleEvent.getState();
    }

    @Override
    public Entity getEntity() {
        return farmlandTrampleEvent.getEntity();
    }

    @Override
    public float getFallDistance() {
        return farmlandTrampleEvent.getFallDistance();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Entity entity = getEntity();
        Level level = entity.level();
        if (level != null && level.isClientSide) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getBlockPos().getCenter(),
                Vec2.ZERO,
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                this.getEntity()
        );
    }

    @Override public String getTextName() {
        return "ForgeFarmlandTrampleEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}