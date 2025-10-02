package xiao.battleroyale.compat.neoforge.event.game.finish;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.GameCompleteData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GameCompleteEvent extends AbstractGameEvent {

    protected final boolean hasWinner;

    public GameCompleteEvent(IGameManager gameManager, boolean hasWinner) {
        super(gameManager);
        this.hasWinner = hasWinner;
    }

    public boolean hasWinner() {
        return this.hasWinner;
    }

    public static GameCompleteEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameCompleteData data)) {
            throw new RuntimeException("Expected GameCompleteData but received: " + customEventData.getClass().getName());
        }
        return new GameCompleteEvent(data.gameManager, data.hasWinner);
    }
}