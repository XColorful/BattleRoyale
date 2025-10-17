package xiao.battleroyale.event.loot;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class LootGenerateEventsHandler extends AbstractEventHandler {

    private static class LootGenerateEventsHandlerHolder {
        private static final LootGenerateEventsHandler INSTANCE = new LootGenerateEventsHandler();
    }

    public static LootGenerateEventsHandler get() {
        return LootGenerateEventsHandlerHolder.INSTANCE;
    }

    private LootGenerateEventsHandler() {
        super(CustomEventType.CUSTOM_GENERATE_EVENT);
    }
}