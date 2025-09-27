package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveFinishEvent extends AbstractGameStatsEvent {

    private @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }
}
