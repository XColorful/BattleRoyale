package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickFinishEvent extends AbstractGameStatsEvent {

    protected final int gameTime;

    public AbstractGameTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager);
        this.gameTime = gameTime;
    }

    public int getGameTime() {
        return this.gameTime;
    }
}
