package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IPlayerLoggedInEvent;

public class ForgePlayerLoggedInEvent extends ForgeEvent implements IPlayerLoggedInEvent {

    protected PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent;

    public ForgePlayerLoggedInEvent(Event event) {
        super(event);
        if (event instanceof PlayerEvent.PlayerLoggedInEvent eventIn) {
            this.playerLoggedInEvent = eventIn;
        } else {
            throw new RuntimeException("Expected PlayerLoggedInEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.PLAYER_LOGGED_IN_EVENT;
    }

    @Override
    public Player getEntity() {
        return playerLoggedInEvent.getEntity();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                Vec2.ZERO,
                (ServerLevel) entity.level(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                entity.getServer(),
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
