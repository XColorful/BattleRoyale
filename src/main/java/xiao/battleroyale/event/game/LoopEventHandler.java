package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.GameManager;

public class LoopEventHandler {

    private LoopEventHandler() {}

    private static class LoopEventHandlerHolder {
        private static final LoopEventHandler INSTANCE = new LoopEventHandler();
    }

    public static LoopEventHandler get() {
        return LoopEventHandlerHolder.INSTANCE;
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
        if (!GameManager.get().isInGame()) {
            unregister();
            return;
        }

        GameManager.get().onGameTick();
    }
}