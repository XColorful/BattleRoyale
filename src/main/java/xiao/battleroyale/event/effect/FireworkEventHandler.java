package xiao.battleroyale.event.effect;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.effect.firework.FireworkManager;

public class FireworkEventHandler {

    private FireworkEventHandler() {}

    private static class FireworkEventHandlerHolder {
        private static final FireworkEventHandler INSTANCE = new FireworkEventHandler();
    }

    public static FireworkEventHandler get() {
        return FireworkEventHandlerHolder.INSTANCE;
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
        FireworkManager.get().onTick();
        if (FireworkManager.get().shouldEnd()) {
            unregister();
        }
    }
}