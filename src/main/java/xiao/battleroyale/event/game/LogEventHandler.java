package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;

/**
 * 监听玩家登出/登入
 */
public class LogEventHandler {

    private static class LogEventHandlerHolder {
        private static final LogEventHandler INSTANCE = new LogEventHandler();
    }

    public static LogEventHandler get() {
        return LogEventHandlerHolder.INSTANCE;
    }

    private LogEventHandler() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    /**
     * 监听玩家登录事件
     * 当玩家登录时，通知TeamManager
     * @param event 玩家登录事件
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            GameManager.get().onPlayerLoggedIn(serverPlayer);
        }
    }

    /**
     * 监听玩家登出事件
     * 当玩家登出时，通知TeamManager
     * @param event 玩家登出事件
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            GameManager.get().onPlayerLoggedOut(serverPlayer);
        }
    }
}