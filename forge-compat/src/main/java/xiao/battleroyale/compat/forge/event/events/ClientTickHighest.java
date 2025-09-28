package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeClientTickEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class ClientTickHighest extends AbstractEventCommon {

    private static class ClientTickHighestHolder {
        private static final ClientTickHighest INSTANCE = new ClientTickHighest();
    }

    public static ClientTickHighest get() {
        return ClientTickHighest.ClientTickHighestHolder.INSTANCE;
    }

    private ClientTickHighest() {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        super.onEvent(event);
    }
}
