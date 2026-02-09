package xiao.battleroyale.event.handler.game;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

/**
 * 监听玩家登出/登入
 */
public class LogEventHandler implements IEventHandler {

    private static class LogEventHandlerHolder {
        private static final LogEventHandler INSTANCE = new LogEventHandler();
    }

    public static LogEventHandler get() {
        return LogEventHandlerHolder.INSTANCE;
    }

    private LogEventHandler() {}

    public static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.PLAYER_LOGGED_IN_EVENT);
        eventRegister.register(get(), EventType.PLAYER_LOGGED_OUT_EVENT);
    }

    public static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.PLAYER_LOGGED_IN_EVENT);
        eventRegister.unregister(get(), EventType.PLAYER_LOGGED_OUT_EVENT);
    }

    @Override public String getEventHandlerName() {
        return "LogEventHandler";
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        switch (eventType) {
            case PLAYER_LOGGED_IN_EVENT -> onPlayerLoggedIn((IPlayerLoggedInEvent) event);
            case PLAYER_LOGGED_OUT_EVENT -> onPlayerLoggedOut((IPlayerLoggedOutEvent) event);
            default -> onReceiveWrongEvent(eventType);
        }
    }

    /**
     * 监听玩家登录事件
     * 当玩家登录时，通知TeamManager
     * @param event 玩家登录事件
     */
    private void onPlayerLoggedIn(IPlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            BattleRoyale.getGameManager().onPlayerLoggedIn(serverPlayer);
        }
    }

    /**
     * 监听玩家登出事件
     * 当玩家登出时，通知TeamManager
     * @param event 玩家登出事件
     */
    private void onPlayerLoggedOut(IPlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            BattleRoyale.getGameManager().onPlayerLoggedOut(serverPlayer);
        }
    }
}