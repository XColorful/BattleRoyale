package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickEvent extends AbstractGameEvent {

    protected final int gameTickTime;

    public AbstractGameTickEvent(IGameManager gameManager, int gameTickTime) {
        super(gameManager);
        this.gameTickTime = gameTickTime;
    }

    public int getGameTickTime() {
        return this.gameTickTime;
    }
}
