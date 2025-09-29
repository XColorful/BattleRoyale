package xiao.battleroyale.compat.forge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameInitData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameEvent;

public class GameInitEvent extends AbstractGameEvent {

    public GameInitEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameInitEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameInitData data)) {
            throw new RuntimeException("Expected GameInitData but received: " + customEventData.getClass().getName());
        }
        return new GameInitEvent(data.gameManager);
    }
}
