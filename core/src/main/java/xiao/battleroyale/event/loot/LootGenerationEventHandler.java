package xiao.battleroyale.event.loot;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.event.EventRegistry;

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
        EventRegistry.register(get(), EventType.SERVER_TICK_EVENT);
    }

    public static void unregister() {
        EventRegistry.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            boolean taskCompletedOrInterrupted = CommonLootManager.get().onTick((IServerTickEvent) event);
            if (taskCompletedOrInterrupted) {
                unregister();
            }
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}