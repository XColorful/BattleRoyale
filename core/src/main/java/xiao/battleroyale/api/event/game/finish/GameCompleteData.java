package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameCompleteData extends AbstractGameEventData {

    public final boolean hasWinner;

    public GameCompleteData(IGameManager gameManager, boolean hasWinner) {
        super(gameManager);
        this.hasWinner = hasWinner;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_COMPLETE_EVENT;
    }
}
