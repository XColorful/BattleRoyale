package xiao.battleroyale.compat.neoforge.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDownFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

public class GamePlayerDownFinishEvent extends AbstractGameStatsEvent {

    protected final GamePlayer gamePlayer;
    protected final LivingEntity livingEntity;
    protected final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
        this.livingDeathEvent = event;
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

    public static GamePlayerDownFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerDownFinishData data)) {
            throw new RuntimeException("Expected GamePlayerDownFinishData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerDownFinishEvent(data.gameManager, data.gamePlayer, data.livingEntity, data.livingDeathEvent);
    }
}