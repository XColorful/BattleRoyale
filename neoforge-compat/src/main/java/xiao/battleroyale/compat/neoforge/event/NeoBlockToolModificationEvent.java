package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IBlockToolModificationEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoBlockToolModificationEvent extends NeoEvent implements IBlockToolModificationEvent {

    protected BlockEvent.BlockToolModificationEvent blockToolModificationEvent;

    public NeoBlockToolModificationEvent(Event event) {
        super(event);
        if (event instanceof BlockEvent.BlockToolModificationEvent eventIn) {
            this.blockToolModificationEvent = eventIn;
        } else {
            throw new RuntimeException("Expected BlockToolModificationEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public LevelAccessor getLevelAccessor() {
        return blockToolModificationEvent.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockToolModificationEvent.getPos();
    }

    @Override
    public BlockState getBlockState() {
        return blockToolModificationEvent.getState();
    }

    @Override
    public @Nullable Player getPlayer() {
        return blockToolModificationEvent.getPlayer();
    }

    @Override
    public UseOnContext getContext() {
        return blockToolModificationEvent.getContext();
    }

    @Override
    public ItemStack getHeldItemStack() {
        return blockToolModificationEvent.getHeldItemStack();
    }

    @Override
    public boolean isSimulated() {
        return blockToolModificationEvent.isSimulated();
    }

    @Override
    public void setFinalState(@Nullable BlockState finalState) {
        blockToolModificationEvent.setFinalState(finalState);
    }

    @Override
    public BlockState getFinalState() {
        return blockToolModificationEvent.getFinalState();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Entity entity = this.getPlayer();
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
                entity
        );
    }

    @Override public EventType getType() {
        return EventType.BLOCK_TOOL_MODIFICATION_EVENT;
    }

    @Override public String getTextName() {
        return "NeoBlockToolModificationEvent";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}