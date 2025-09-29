package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameInitFinishData extends AbstractGameStatsEventData {

    public GameInitFinishData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_INIT_FINISH_EVENT;
    }
}
