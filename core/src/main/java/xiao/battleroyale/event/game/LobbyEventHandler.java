package xiao.battleroyale.event.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.event.EventRegistry;

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
        EventRegistry.register(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGHEST, false);
    }

    public static void unregister() {
        EventRegistry.unregister(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGHEST, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.LIVING_DAMAGE_EVENT){
            if (SpawnManager.get().canMuteki(((ILivingDamageEvent) event).getEntity())) {
                event.setCanceled(true);
            }
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}