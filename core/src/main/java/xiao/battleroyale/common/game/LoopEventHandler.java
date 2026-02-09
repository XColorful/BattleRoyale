package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.game.IGameManager;

public class LoopEventHandler implements IEventHandler {

    private LoopEventHandler() {}

    private static class LoopEventHandlerHolder {
        private static final LoopEventHandler INSTANCE = new LoopEventHandler();
    }

    public static LoopEventHandler get() {
        return LoopEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "LoopEventHandler";
    }

    protected static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.SERVER_TICK_EVENT);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            IGameManager gameManager = BattleRoyale.getGameManager();
            if (!gameManager.isInGame()) {
                unregister();
            }
            gameManager.addGameTimeAndTick();
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
}