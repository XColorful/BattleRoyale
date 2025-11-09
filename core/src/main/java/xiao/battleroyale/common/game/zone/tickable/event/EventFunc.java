package xiao.battleroyale.common.game.zone.tickable.event;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.game.zone.CustomZoneEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.event.EventPoster;

public class EventFunc extends AbstractEventFunc {

    public EventFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                     String protocol, @NotNull JsonObject jsonTag) {
        super(moveDelay, moveTime, tickFreq, tickOffset, protocol, jsonTag);
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.EVENT;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                LivingEntity livingEntity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                EventPoster.postEvent(new CustomZoneEvent(gameManager, zoneTickContext, this.protocol, this.jsonTag, gamePlayer, livingEntity));
            }
        }
    }
}
