package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameInitData extends AbstractGameEventData {

    public GameInitData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_INIT_EVENT;
    }
}
