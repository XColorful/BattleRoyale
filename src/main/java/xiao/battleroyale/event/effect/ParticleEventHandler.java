package xiao.battleroyale.event.effect;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.effect.particle.ParticleManager;

public class ParticleEventHandler {

    private ParticleEventHandler() {}

    private static class ParticleEventHandlerHolder {
        private static final ParticleEventHandler INSTANCE = new ParticleEventHandler();
    }

    public static ParticleEventHandler get() {
        return ParticleEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    // 仅限ParticleManager调用，内部维护是否已经注册
    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ParticleManager.get().onTick();
    }
}
