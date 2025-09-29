package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameTickFinishData extends AbstractGameTickFinishEventData {

    public GameTickFinishData(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_TICK_FINISH_EVENT;
    }
}
