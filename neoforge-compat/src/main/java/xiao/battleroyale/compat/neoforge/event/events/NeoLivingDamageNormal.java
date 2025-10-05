package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoLivingDamageEvent;

public class NeoLivingDamageNormal extends AbstractNeoEventCommon {

    private static class NeoLivingDamageNormalHolder {
        private static final NeoLivingDamageNormal INSTANCE = new NeoLivingDamageNormal();
    }

    public static NeoLivingDamageNormal get() {
        return NeoLivingDamageNormalHolder.INSTANCE;
    }

    private NeoLivingDamageNormal() {
        super(EventType.LIVING_DAMAGE_EVENT);
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
        return new NeoLivingDamageEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onLivingDamageEvent(LivingDamageEvent.Pre event) {
        super.onEvent(event);
    }
}