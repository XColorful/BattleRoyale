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
import xiao.battleroyale.api.event.IPlayerOpenContainerEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoPlayerOpenContainerEvent extends NeoEvent implements IPlayerOpenContainerEvent {

    protected PlayerContainerEvent.Open playerOpenContainerEvent;

    public NeoPlayerOpenContainerEvent(Event event) {
        super(event);
        if (event instanceof PlayerContainerEvent.Open eventIn) {
            this.playerOpenContainerEvent = eventIn;
        } else {
            throw new RuntimeException("Expected PlayerContainerEvent.Open but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_OPEN_CONTAINER_EVENT;
    }

    @Override
    public Player getPlayer() {
        return playerOpenContainerEvent.getEntity();
    }

    @Override
    public AbstractContainerMenu getContainer() {
        return playerOpenContainerEvent.getContainer();
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