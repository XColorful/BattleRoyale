package xiao.battleroyale.compat.neoforge.event.game;

import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameEvent extends AbstractCancellableEvent {

    protected final IGameManager gameManager;

    public AbstractGameEvent(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    public boolean isCancelable() {
        return true;
    }
}