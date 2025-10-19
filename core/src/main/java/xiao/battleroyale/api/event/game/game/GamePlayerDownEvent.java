package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDownEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @NotNull final LivingEntity livingEntity;
    protected final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
        this.livingDeathEvent = event;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DOWN_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }
}
