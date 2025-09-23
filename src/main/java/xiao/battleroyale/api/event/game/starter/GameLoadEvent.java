package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLoadEvent extends AbstractGameEvent {

    public GameLoadEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
