package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

public class SyncEventHandler {

    private static SyncEventHandler instance;

    private SyncEventHandler() {}

    public static SyncEventHandler get() {
        if (instance == null) {
            instance = new SyncEventHandler();
        }
        return instance;
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.info("SyncEventHandler registered");
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        instance = null;
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
