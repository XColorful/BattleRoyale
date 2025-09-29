package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class BoostFunc extends AbstractSimpleFunc {

    public final int boost;

    public BoostFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, int boost) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.boost = boost;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                EffectManager.get().addBoost(gamePlayer.getPlayerUUID(), boost, zoneTickContext.serverLevel);
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.BOOST;
    }
}