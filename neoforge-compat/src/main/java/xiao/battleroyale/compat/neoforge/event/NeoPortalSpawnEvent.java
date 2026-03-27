package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IPortalSpawnEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoPortalSpawnEvent extends NeoEvent implements IPortalSpawnEvent {

    protected BlockEvent.PortalSpawnEvent portalSpawnEvent;

    public NeoPortalSpawnEvent(Event event) {
        super(event);
        if (event instanceof BlockEvent.PortalSpawnEvent eventIn) {
            this.portalSpawnEvent = eventIn;
        } else {
            throw new RuntimeException("Expected PortalSpawnEvent but received: " + event.getClass().getName());
        }
    }

    @Override public EventType getType() {
        return EventType.PORTAL_SPAWN_EVENT;
    }

    @Override
    public LevelAccessor getLevelAccessor() {
        return portalSpawnEvent.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return portalSpawnEvent.getPos();
    }

    @Override
    public BlockState getBlockState() {
        return portalSpawnEvent.getState();
    }

    @Override
    public PortalShape getPortalSize() {
        return portalSpawnEvent.getPortalSize();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @Nullable ServerLevel serverLevel = getServerLevel();
        if (serverLevel == null) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getBlockPos().getCenter(),
                Vec2.ZERO,
                serverLevel,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                serverLevel.getServer(),
                null
        );
    }

    @Override public String getTextName() {
        return "NeoPortalSpawnEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}