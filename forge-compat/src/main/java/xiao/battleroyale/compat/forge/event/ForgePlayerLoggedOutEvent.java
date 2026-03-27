package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IPlayerLoggedOutEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgePlayerLoggedOutEvent extends ForgeEvent implements IPlayerLoggedOutEvent {

    protected PlayerEvent.PlayerLoggedOutEvent playerLoggedOutEvent;

    public ForgePlayerLoggedOutEvent(Event event) {
        super(event);
        if (event instanceof PlayerEvent.PlayerLoggedOutEvent eventIn) {
            this.playerLoggedOutEvent = eventIn;
        } else {
            throw new RuntimeException("Expected PlayerLoggedOutEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.PLAYER_LOGGED_OUT_EVENT;
    }
    
    @Override
    public Player getEntity() {
        return playerLoggedOutEvent.getEntity();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        Level level = entity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
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
        return this.getEntity().getName().getString();
    }
    @Override public Component getDisplayName() {
        return this.getEntity().getDisplayName();
    }
}
