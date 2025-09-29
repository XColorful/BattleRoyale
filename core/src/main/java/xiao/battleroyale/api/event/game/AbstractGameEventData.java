package xiao.battleroyale.api.event.game;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameEventData implements ICustomEventData {

    public final IGameManager gameManager;

    public AbstractGameEventData(IGameManager gameManager) {
        this.gameManager = gameManager;
    }
}
