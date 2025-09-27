package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameCompleteEvent extends AbstractGameEvent {

    private final boolean hasWinner;

    public GameCompleteEvent(IGameManager gameManager, boolean hasWinner) {
        super(gameManager);
        this.hasWinner = hasWinner;
    }

    public boolean hasWinner() {
        return this.hasWinner;
    }
}
