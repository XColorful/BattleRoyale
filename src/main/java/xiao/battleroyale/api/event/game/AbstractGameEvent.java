package xiao.battleroyale.api.event.game;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameEvent extends Event {

    protected final IGameManager gameManager;

    public AbstractGameEvent(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
