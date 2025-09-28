package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLoadFinishData extends AbstractGameStatsEventData {

    public GameLoadFinishData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOAD_FINISH_EVENT;
    }
}
