package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.GameManager;

public class LoopEventHandler {

    private static LoopEventHandler instance;

    private LoopEventHandler() {}

    public static LoopEventHandler getInstance() {
        if (instance == null) {
            instance = new LoopEventHandler();
        }
        return instance;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(getInstance());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(getInstance());
        instance = null;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            if (!GameManager.get().isInGame()) {
                unregister();
            }
        }
    }
}
