package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IServerChatEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoServerChatEvent extends NeoEvent implements IServerChatEvent {

    protected ServerChatEvent serverChatEvent;

    public NeoServerChatEvent(Event event) {
        super(event);
        if (event instanceof ServerChatEvent eventIn) {
            this.serverChatEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ServerChatEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_CHAT_EVENT;
    }

    @Override
    public ServerPlayer getPlayer() {
        return serverChatEvent.getPlayer();
    }

    @Override
    public String getRawText() {
        return serverChatEvent.getRawText();
    }

    @Override
    public Component getMessage() {
        return serverChatEvent.getMessage();
    }

    @Override
    public void setMessage(Component message) {
        serverChatEvent.setMessage(message);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        ServerPlayer player = this.getPlayer();
        Level level = player.level();
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

    @Override
    public String getTextName() {
        return this.getPlayer().getName().getString();
    }

    @Override
    public Component getDisplayName() {
        return this.getPlayer().getDisplayName();
    }
}