package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;

public class GameTickEvent extends AbstractGameTickEvent {

    public GameTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
}
