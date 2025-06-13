package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

public class SyncEventHandler {

    private SyncEventHandler() {}

    private static class SyncEventHandlerHolder {
        private static final SyncEventHandler INSTANCE = new SyncEventHandler();
    }

    public static SyncEventHandler get() {
        return SyncEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.info("SyncEventHandler registered");
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        BattleRoyale.LOGGER.info("SyncEventHandler unregistered");
    }

    @SubscribeEvent
    public void onGameTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        GameManager.get().syncInfo();
    }
}