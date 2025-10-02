package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingDeathEvent;

public class NeoLivingDeathHigh extends AbstractNeoEventCommon {

    private static class NeoLivingDeathHighHolder {
        private static final NeoLivingDeathHigh INSTANCE = new NeoLivingDeathHigh();
    }

    public static NeoLivingDeathHigh get() {
        return NeoLivingDeathHighHolder.INSTANCE;
    }

    private NeoLivingDeathHigh() {
        super(EventType.LIVING_DEATH_EVENT);
    }

    @Override
    protected void registerToNeo() {
        NeoForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToNeo() {
        NeoForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected NeoEvent getNeoEventType(Event event) {
        return new NeoLivingDeathEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        super.onEvent(event);
    }
}