package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDamageEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final ILivingDamageEvent livingDamageEvent;

    public GamePlayerDamageEvent(IGameManager gameManager, @NotNull final GamePlayer gamePlayer, @Nullable ILivingDamageEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingDamageEvent = event;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DAMAGE_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable ILivingDamageEvent getLivingDamageEvent() {
        return this.livingDamageEvent;
    }
}
