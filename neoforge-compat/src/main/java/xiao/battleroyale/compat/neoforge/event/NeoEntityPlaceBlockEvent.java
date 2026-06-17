package xiao.battleroyale.compat.neoforge.event;

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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEntityPlaceBlockEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoEntityPlaceBlockEvent extends NeoEvent implements IEntityPlaceBlockEvent {

    protected BlockEvent.EntityPlaceEvent entityPlaceBlockEvent;

    public NeoEntityPlaceBlockEvent(Event event) {
        super(event);
        if (event instanceof BlockEvent.EntityPlaceEvent eventIn) {
            this.entityPlaceBlockEvent = eventIn;
        } else {
            throw new RuntimeException("Expected EntityPlaceEvent but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.ENTITY_PLACE_BLOCK_EVENT;
    }

    @Override
    public LevelAccessor getLevelAccessor() {
        return entityPlaceBlockEvent.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return entityPlaceBlockEvent.getPos();
    }

    @Override
    public BlockState getBlockState() {
        return entityPlaceBlockEvent.getState();
    }

    @Override
    public @Nullable Entity getEntity() {
        return entityPlaceBlockEvent.getEntity();
    }

    @Override
    public BlockState getPlacedBlock() {
        return entityPlaceBlockEvent.getPlacedBlock();
    }

    @Override
    public BlockState getPlacedAgainst() {
        return entityPlaceBlockEvent.getPlacedAgainst();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Entity entity = this.getEntity();
        Level level = entity != null ? entity.level() : null;
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.atCenterOf(this.getBlockPos()),
                entity != null ? entity.getRotationVector() : Vec2.ZERO,
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                this.getEntity()
        );
    }

    @Override public String getTextName() {
        return "NeoEntityPlaceBlockEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}