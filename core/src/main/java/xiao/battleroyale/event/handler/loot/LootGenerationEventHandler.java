package xiao.battleroyale.event.handler.loot;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

public class LootGenerationEventHandler implements IEventHandler {

    private LootGenerationEventHandler() {}

    private static class LootGenerationEventHandlerHolder {
        private static final LootGenerationEventHandler INSTANCE = new LootGenerationEventHandler();
    }

    public static LootGenerationEventHandler get() {
        return LootGenerationEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "LootGenerationEventHandler";
    }

    public static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            boolean taskCompletedOrInterrupted = BattleRoyale.getCommonLootManager().onLootTick((IServerTickEvent) event);
            if (taskCompletedOrInterrupted) {
                unregister();
            }
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}