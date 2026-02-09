package xiao.battleroyale.event.handler.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

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
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.SERVER_TICK_EVENT);
        eventRegister.register(get(), EventType.LIVING_ATTACK_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
    }

    public static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
        eventRegister.unregister(get(), EventType.LIVING_ATTACK_EVENT, xiao.battleroyale.api.event.EventPriority.HIGH, false);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        switch (eventType) {
            case SERVER_TICK_EVENT -> BattleRoyale.getEffectManager().getMutekiManager().onTick();
            case LIVING_ATTACK_EVENT -> {
                if (BattleRoyale.getEffectManager().getMutekiManager().canMuteki(((ILivingAttackEvent) event).getEntity())) {
                    event.setCanceled(true);
                }
            }
            default -> onReceiveWrongEvent(eventType);
        }
    }
}
