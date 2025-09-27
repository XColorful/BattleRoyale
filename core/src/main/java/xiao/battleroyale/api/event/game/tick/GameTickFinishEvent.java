package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;

public class GameTickFinishEvent extends AbstractGameTickFinishEvent {

    public GameTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
}
