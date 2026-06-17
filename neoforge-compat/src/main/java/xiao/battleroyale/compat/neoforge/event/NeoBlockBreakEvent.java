package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IBlockBreakEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoBlockBreakEvent extends NeoEvent implements IBlockBreakEvent {

    protected BreakBlockEvent blockBreakEvent;

    public NeoBlockBreakEvent(Event event) {
        super(event);
        if (event instanceof BreakBlockEvent eventIn) {
            this.blockBreakEvent = eventIn;
        } else {
            throw new RuntimeException("Expected BreakBlockEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.BLOCK_BREAK_EVENT;
    }

    @Override public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        if (cancel && !blockBreakEvent.getLevel().isClientSide()) {
            blockBreakEvent.setNotifyClient(true);
        }
    }

    @Override
    public LevelAccessor getLevelAccessor() {
        return blockBreakEvent.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockBreakEvent.getPos();
    }

    @Override
    public BlockState getBlockState() {
        return blockBreakEvent.getState();
    }

    @Override
    public Player getPlayer() {
        return blockBreakEvent.getPlayer();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Entity entity = this.getPlayer();
        Level level = entity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.atCenterOf(this.getBlockPos()),
                entity.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                entity
        );
    }

    @Override public String getTextName() {
        return "NeoBlockBreakEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}