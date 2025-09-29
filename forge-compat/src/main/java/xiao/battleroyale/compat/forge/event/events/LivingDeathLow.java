package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingDeathEvent;

public class LivingDeathLow extends AbstractEventCommon {

    private static class LivingDeathLowHolder {
        private static final LivingDeathLow INSTANCE = new LivingDeathLow();
    }

    public static LivingDeathLow get() {
        return LivingDeathLowHolder.INSTANCE;
    }

    private LivingDeathLow() {
        super(EventType.LIVING_DEATH_EVENT);
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
        return new ForgeLivingDeathEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        super.onEvent(event);
    }
}