package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;

public class GamePlayerDownFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;
    protected final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = GameUtils.getLivingEntity(gameManager.getServerLevel(), gamePlayer.getPlayerUUID());
        this.livingDeathEvent = event;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }
}
