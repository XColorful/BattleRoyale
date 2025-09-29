package xiao.battleroyale.event.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.event.EventRegistry;

public class MutekiEventHandler implements IEventHandler {

    private static class MutekiEventHandlerHolder {
        private static final MutekiEventHandler INSTANCE = new MutekiEventHandler();
    }

    public static MutekiEventHandler get() {
        return MutekiEventHandlerHolder.INSTANCE;
    }

    private MutekiEventHandler() {}

    @Override public String getEventHandlerName() {
        return "MutekiEventHandler";
    }

    public static void register() {
        EventRegistry.register(get(), EventType.SERVER_TICK_EVENT);
        EventRegistry.register(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
    }

    public static void unregister() {
        EventRegistry.unregister(get(), EventType.SERVER_TICK_EVENT);
        EventRegistry.unregister(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        switch (eventType) {
            case SERVER_TICK_EVENT -> MutekiManager.get().onTick();
            case LIVING_DAMAGE_EVENT -> {
                if (MutekiManager.get().canMuteki(((ILivingDamageEvent) event).getEntity())) {
                    event.setCanceled(true);
                }
            }
            default -> BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}
