package xiao.battleroyale.compat.neoforge.event.game.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDeathFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

public class GamePlayerDeathFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDeathFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @Nullable ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingDeathEvent = event;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }

    public static GamePlayerDeathFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerDeathFinishData data)) {
            throw new RuntimeException("Expected GamePlayerDeathFinishData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerDeathFinishEvent(data.gameManager, data.gamePlayer, data.livingDeathEvent);
    }
}