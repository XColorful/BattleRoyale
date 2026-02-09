package xiao.battleroyale.event.handler.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

/**
 * 只用于实现Lobby内无敌
 * 优先级设置为HIGHEST，确保在其他伤害处理前执行
 * 注册该事件默认开启大厅无敌
 */
public class LobbyEventHandler implements IEventHandler {

    private static class LobbyEventHandlerHolder {
        private static final LobbyEventHandler INSTANCE = new LobbyEventHandler();
    }

    public static LobbyEventHandler get() {
        return LobbyEventHandlerHolder.INSTANCE;
    }

    private LobbyEventHandler() {}

    @Override public String getEventHandlerName() {
        return "LobbyEventHandlerHolder";
    }

    public static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.LIVING_ATTACK_EVENT, xiao.battleroyale.api.event.EventPriority.HIGHEST, false);
    }

    public static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.LIVING_ATTACK_EVENT, xiao.battleroyale.api.event.EventPriority.HIGHEST, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.LIVING_ATTACK_EVENT){
            if (BattleRoyale.getGameManager().getGameLobbyManager().canMuteki(((ILivingAttackEvent) event).getEntity())) {
                event.setCanceled(true);
            }
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}