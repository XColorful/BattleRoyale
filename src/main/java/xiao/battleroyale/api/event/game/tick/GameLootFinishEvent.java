package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;

public class GameLootFinishEvent extends AbstractGameTickFinishEvent {

    public GameLootFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
}
