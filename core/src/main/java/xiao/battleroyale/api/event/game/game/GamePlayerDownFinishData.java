package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDownFinishData extends AbstractGameStatsEventData {

    public final GamePlayer gamePlayer;
    public final LivingEntity livingEntity;
    public final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownFinishData(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
        this.livingDeathEvent = event;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT;
    }
}
