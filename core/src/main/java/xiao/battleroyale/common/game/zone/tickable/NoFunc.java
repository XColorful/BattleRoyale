package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class NoFunc extends AbstractSimpleFunc {

    public NoFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        return;
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.NO_FUNC;
    }
}
