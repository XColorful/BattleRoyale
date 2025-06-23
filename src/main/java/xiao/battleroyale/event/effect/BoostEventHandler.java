package xiao.battleroyale.event.effect;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.effect.boost.BoostManager;

public class BoostEventHandler {

    private BoostEventHandler() {}

    private static class BoostEventHandlerHolder {
        private static final BoostEventHandler INSTANCE = new BoostEventHandler();
    }

    public static BoostEventHandler get() {
        return BoostEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        BoostManager.get().onTick();
    }
}
