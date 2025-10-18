package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameInitFinishEvent extends AbstractGameStatsEvent {

    public GameInitFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_INIT_FINISH_EVENT;
    }
}
