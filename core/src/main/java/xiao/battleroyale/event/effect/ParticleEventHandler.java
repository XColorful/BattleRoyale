package xiao.battleroyale.event.effect;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.common.effect.particle.ParticleManager;
import xiao.battleroyale.event.EventRegistry;

public class ParticleEventHandler implements IEventHandler {

    private ParticleEventHandler() {}

    private static class ParticleEventHandlerHolder {
        private static final ParticleEventHandler INSTANCE = new ParticleEventHandler();
    }

    public static ParticleEventHandler get() {
        return ParticleEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "ParticleEventHandler";
    }

    public static void register() {
        EventRegistry.register(get(), EventType.SERVER_TICK_EVENT);
    }

    // 仅限ParticleManager调用，内部维护是否已经注册
    public static void unregister() {
        EventRegistry.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            ParticleManager.get().onTick();
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }
}
