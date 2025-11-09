package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeClientTickEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class ClientTickLow extends AbstractEventCommon {

    private static class ClientTickLowHolder {
        private static final ClientTickLow INSTANCE = new ClientTickLow();
    }

    public static ClientTickLow get() {
        return ClientTickLow.ClientTickLowHolder.INSTANCE;
    }

    private ClientTickLow() {
        super(EventType.CLIENT_TICK_EVENT);
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
        return new ForgeClientTickEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        super.onEvent(event);
    }
}