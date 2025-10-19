package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull
    final GamePlayer gamePlayer;

    public GamePlayerReviveFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }
}
