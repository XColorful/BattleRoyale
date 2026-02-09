package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;

public class GamePlayerDamageFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;
    protected @Nullable final ILivingDamageEvent livingDamageEvent;

    public GamePlayerDamageFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull ILivingDamageEvent livingDamageEvent) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = GameUtils.getLivingEntity(gameManager.getServerLevel(), gamePlayer.getPlayerUUID());
        this.livingDamageEvent = livingDamageEvent;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DAMAGE_FINISH_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public @Nullable ILivingDamageEvent getLivingDamageEvent() {
        return this.livingDamageEvent;
    }
}
