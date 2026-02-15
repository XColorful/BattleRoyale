package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickFinishEvent extends AbstractGameStatsEvent {

    protected final int gameTickTime;

    public AbstractGameTickFinishEvent(IGameManager gameManager, int gameTickTime) {
        super(gameManager);
        this.gameTickTime = gameTickTime;
    }

    public int getGameTickTime() {
        return this.gameTickTime;
    }
}
