package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLoadFinishEvent extends AbstractGameStatsEvent {

    public GameLoadFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
