package xiao.battleroyale.compat.forge.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.game.GamePlayerReviveData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.forge.event.game.AbstractGameEvent;

public class GamePlayerReviveEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public static GamePlayerReviveEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerReviveData data)) {
            throw new RuntimeException("Expected GamePlayerReviveData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerReviveEvent(data.gameManager, data.gamePlayer);
    }
}
