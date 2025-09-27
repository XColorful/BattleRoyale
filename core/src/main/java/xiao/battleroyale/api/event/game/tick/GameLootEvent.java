package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;

public class GameLootEvent extends AbstractGameTickEvent {

    public GameLootEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
}
