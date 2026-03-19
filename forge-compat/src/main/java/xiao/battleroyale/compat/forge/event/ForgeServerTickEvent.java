package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IServerTickEvent;

public class ForgeServerTickEvent extends ForgeEvent implements IServerTickEvent {

    protected TickEvent.ServerTickEvent serverTickEvent;

    public ForgeServerTickEvent(Event event) {
        super(event);
        if (event instanceof TickEvent.ServerTickEvent eventIn) {
            this.serverTickEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ServerTickEvent but received: " + event.getClass().getName());
        }
    }
    @Override public EventType getType() {
        return EventType.SERVER_TICK_EVENT;
    }

    @Override
    public MinecraftServer getServer() {
        return serverTickEvent.getServer();
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        MinecraftServer server = this.getServer();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                server.overworld(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                server,
                null
        );
    }

    @Override public String getTextName() {
        return "ForgeServerTickEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
