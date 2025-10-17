package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameCompleteEvent extends AbstractGameEvent {

    protected final boolean hasWinner;

    public GameCompleteEvent(IGameManager gameManager, boolean hasWinner) {
        super(gameManager);
        this.hasWinner = hasWinner;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_COMPLETE_EVENT;
    }

    public boolean hasWinner() {
        return this.hasWinner;
    }
}
