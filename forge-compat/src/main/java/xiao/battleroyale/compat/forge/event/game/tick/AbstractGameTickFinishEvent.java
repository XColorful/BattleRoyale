package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

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
