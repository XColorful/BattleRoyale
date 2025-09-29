package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveFinishData extends AbstractGameStatsEventData {

    public @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveFinishData(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT;
    }
}
