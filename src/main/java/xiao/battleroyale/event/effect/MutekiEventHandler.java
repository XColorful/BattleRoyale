package xiao.battleroyale.event.effect;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.effect.muteki.MutekiManager;

public class MutekiEventHandler {

    private MutekiEventHandler() {}

    private static class MutekiEventHandlerHolder {
        private static final MutekiEventHandler INSTANCE = new MutekiEventHandler();
    }

    public static MutekiEventHandler get() {
        return MutekiEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.debug("MutekiEventHandler registered");
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        BattleRoyale.LOGGER.debug("MutekiEventHandler unregistered");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MutekiManager.get().onTick();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {
        if (MutekiManager.get().canMuteki(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
