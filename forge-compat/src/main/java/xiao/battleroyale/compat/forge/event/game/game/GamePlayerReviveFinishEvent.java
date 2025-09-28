package xiao.battleroyale.compat.forge.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.game.GamePlayerReviveFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class GamePlayerReviveFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public static GamePlayerReviveFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerReviveFinishData data)) {
            throw new RuntimeException("Expected GamePlayerReviveFinishData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerReviveFinishEvent(data.gameManager, data.gamePlayer);
    }
}
