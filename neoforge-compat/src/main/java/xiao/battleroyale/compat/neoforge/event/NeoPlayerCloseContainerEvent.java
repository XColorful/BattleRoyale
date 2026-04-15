package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IPlayerCloseContainerEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoPlayerCloseContainerEvent extends NeoEvent implements IPlayerCloseContainerEvent {

    protected PlayerContainerEvent.Close playerCloseContainerEvent;

    public NeoPlayerCloseContainerEvent(Event event) {
        super(event);
        if (event instanceof PlayerContainerEvent.Close eventIn) {
            this.playerCloseContainerEvent = eventIn;
        } else {
            throw new RuntimeException("Expected PlayerContainerEvent.Close but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_CLOSE_CONTAINER_EVENT;
    }

    @Override
    public Player getPlayer() {
        return playerCloseContainerEvent.getEntity();
    }

    @Override
    public AbstractContainerMenu getContainer() {
        return playerCloseContainerEvent.getContainer();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Player player = this.getPlayer();
        Level level = player.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                player.position(),
                player.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                player
        );
    }

    @Override public String getTextName() {
        return this.getPlayer().getName().getString();
    }

    @Override public Component getDisplayName() {
        return this.getPlayer().getDisplayName();
    }
}