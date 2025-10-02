package xiao.battleroyale.compat.neoforge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameLoadData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GameLoadEvent extends AbstractGameEvent {

    public GameLoadEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameLoadEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLoadData data)) {
            throw new RuntimeException("Expected GameLoadData but received: " + customEventData.getClass().getName());
        }
        return new GameLoadEvent(data.gameManager);
    }
}