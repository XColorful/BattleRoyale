package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeServerTickEvent;

public class ServerTickLow extends AbstractEventCommon {

    private static class ServerTickLowHolder {
        private static final ServerTickLow INSTANCE = new ServerTickLow();
    }

    public static ServerTickLow get() {
        return ServerTickLow.ServerTickLowHolder.INSTANCE;
    }

    private ServerTickLow() {
        super(EventType.SERVER_TICK_EVENT);
    }

    @Override
    protected void registerToForge() {
        MinecraftForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToForge() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected ForgeEvent getForgeEventType(Event event) {
        return new ForgeServerTickEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        super.onEvent(event);
    }
}