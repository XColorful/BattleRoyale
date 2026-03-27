package xiao.battleroyale.compat.forge.event;

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
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IBlockBreakEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeBlockBreakEvent extends ForgeEvent implements IBlockBreakEvent {

    protected BlockEvent.BreakEvent blockBreakEvent;

    public ForgeBlockBreakEvent(Event event) {
        super(event);
        if (event instanceof BlockEvent.BreakEvent eventIn) {
            this.blockBreakEvent = eventIn;
        } else {
            throw new RuntimeException("Expected BreakEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.BLOCK_BREAK_EVENT;
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
        Entity entity = getPlayer();
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
                entity
        );
    }

    @Override public String getTextName() {
        return "ForgeBlockBreakEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
