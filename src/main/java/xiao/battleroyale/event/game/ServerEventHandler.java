package xiao.battleroyale.event.game;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.loot.GameLootManager;

public class ServerEventHandler {

    private static class ServerEventHandlerHolder {
        private static final ServerEventHandler INSTANCE = new ServerEventHandler();
    }

    public static ServerEventHandler get() {
        return ServerEventHandlerHolder.INSTANCE;
    }

    private ServerEventHandler() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    /**
     * 主要用于单人游戏退出重新游戏
     * @param event 服务器关闭事件
     */
    @SubscribeEvent
    public void onServerStopping(ServerStoppedEvent event) {
        GameManager.get().onServerStopping();
        GameLootManager.get().awaitTerminationOnShutdown();
    }
}
