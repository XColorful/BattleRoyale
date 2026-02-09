package xiao.battleroyale.common.server.utility;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

/**
 * 只用于实现生存模式大厅内无敌
 * 优先级设置为HiGHEST，确保在其他伤害处理前执行
 *  * 注册该事件默认开启大厅无敌
 */
public class SurvivalLobbyEventHandler implements IEventHandler {

    private static class SurvivalLobbyEventHandlerHolder {
        private static final SurvivalLobbyEventHandler INSTANCE = new SurvivalLobbyEventHandler();
    }

    public static SurvivalLobbyEventHandler get() {
        return SurvivalLobbyEventHandlerHolder.INSTANCE;
    }

    private SurvivalLobbyEventHandler() {}

    @Override public String getEventHandlerName() {
        return String.format("%s:SurvivalLobbyEventHandler", BattleRoyale.MOD_ID);
    }

    protected static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.LIVING_ATTACK_EVENT, EventPriority.HIGHEST, false);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.LIVING_ATTACK_EVENT, EventPriority.HIGHEST, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event){
        if (eventType == EventType.LIVING_ATTACK_EVENT){
            if (SurvivalLobby.get().canMuteki(((ILivingAttackEvent) event).getEntity())) {
                event.setCanceled(true);
            }
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}
