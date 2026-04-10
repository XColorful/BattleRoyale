package xiao.battleroyale.api.event;

import xiao.battleroyale.BattleRoyale;

/**
 * @param <Y> 事件类型枚举 (EventType 或 CustomEventType)
 * @param <E> 事件实例类型 (IEvent 或 ICustomEvent)
 */
public interface IHandler<Y, E extends IEvent> {
    String getEventHandlerName();

    void handleEvent(Y type, E event);

    default void onReceiveWrongEvent(Y eventType) {
        BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
    }
}