package xiao.battleroyale.compat.forge.event.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;

public class ServerTickNormal extends AbstractEventCommon {

    private static class ServerTickNormalHolder {
        private static final ServerTickNormal INSTANCE = new ServerTickNormal();
    }

    public static ServerTickNormal get() {
        return ServerTickNormalHolder.INSTANCE;
    }

    private ServerTickNormal() {
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

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        super.onEvent(event);
    }
}
