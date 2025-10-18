package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.event.EventRegister;

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
        EventRegister.register(get(), EventType.SERVER_TICK_EVENT);
        EventRegister.register(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
        EventRegister.unregister(get(), EventType.LIVING_DAMAGE_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
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
