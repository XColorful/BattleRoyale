package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgeLivingDeathEvent;

public class LivingDeathHigh extends AbstractEventCommon {

    private static class LivingDeathHighHolder {
        private static final LivingDeathHigh INSTANCE = new LivingDeathHigh();
    }

    public static LivingDeathHigh get() {
        return LivingDeathHighHolder.INSTANCE;
    }

    private LivingDeathHigh() {
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

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        super.onEvent(event);
    }
}