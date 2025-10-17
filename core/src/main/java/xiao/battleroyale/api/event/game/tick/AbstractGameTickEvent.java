package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickEvent extends AbstractGameEvent {

    protected final int gameTime;

    public AbstractGameTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager);
        this.gameTime = gameTime;
    }

    public int getGameTime() {
        return this.gameTime;
    }
}
