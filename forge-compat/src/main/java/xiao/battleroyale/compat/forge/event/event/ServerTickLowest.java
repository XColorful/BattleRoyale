package xiao.battleroyale.compat.forge.event.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;

public class ServerTickLowest extends AbstractEventCommon {

    private static class ServerTickLowestHolder {
        private static final ServerTickLowest INSTANCE = new ServerTickLowest();
    }

    public static ServerTickLowest get() {
        return ServerTickLowest.ServerTickLowestHolder.INSTANCE;
    }

    private ServerTickLowest() {
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

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        super.onEvent(event);
    }
}