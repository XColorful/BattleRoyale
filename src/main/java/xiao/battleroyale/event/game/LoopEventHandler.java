package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.GameManager;

public class LoopEventHandler {

    private static LoopEventHandler instance;

    private LoopEventHandler() {}

    public static LoopEventHandler get() {
        if (instance == null) {
            instance = new LoopEventHandler();
        }
        return instance;
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        instance = null;
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
