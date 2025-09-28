package xiao.battleroyale.event.server;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.common.server.utility.SurvivalLobby;
import xiao.battleroyale.event.EventRegistry;

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
        return "SurvivalLobbyEventHandler";
    }

    public static void register() {
        EventRegistry.register(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.HIGHEST, false);
    }

    public static void unregister() {
        EventRegistry.unregister(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.HIGHEST, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event){
        if (eventType == EventType.LIVING_DAMAGE_EVENT){
            if (SurvivalLobby.get().canMuteki(((ILivingDamageEvent) event).getEntity())) {
                event.setCanceled(true);
            }
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}
