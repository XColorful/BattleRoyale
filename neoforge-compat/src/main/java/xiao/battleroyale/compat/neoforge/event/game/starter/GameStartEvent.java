package xiao.battleroyale.compat.neoforge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameStartData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GameStartEvent extends AbstractGameEvent {

    public GameStartEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameStartEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameStartData data)) {
            throw new RuntimeException("Expected GameStartData but received: " + customEventData.getClass().getName());
        }
        return new GameStartEvent(data.gameManager);
    }
}