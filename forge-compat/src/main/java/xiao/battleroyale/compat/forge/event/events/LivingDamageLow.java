package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingDamageEvent;

public class LivingDamageLow extends AbstractEventCommon {

    private static class LivingDamageLowHolder {
        private static final LivingDamageLow INSTANCE = new LivingDamageLow();
    }

    public static LivingDamageLow get() {
        return LivingDamageLowHolder.INSTANCE;
    }

    private LivingDamageLow() {
        super(EventType.LIVING_DAMAGE_EVENT);
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
        return new ForgeLivingDamageEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onLivingDamageEvent(LivingDamageEvent event) {
        super.onEvent(event);
    }
}
