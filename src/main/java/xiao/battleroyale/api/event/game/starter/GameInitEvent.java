package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameInitEvent extends AbstractGameEvent {

    public GameInitEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
