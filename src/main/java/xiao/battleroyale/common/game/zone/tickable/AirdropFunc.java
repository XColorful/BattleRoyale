package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class AirdropFunc extends AbstractSimpleFunc {

    public AirdropFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public void initFunc(ZoneContext zoneContext) {
        // TODO 预处理
        this.ready = true;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        return;
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.AIRDROP;
    }
}
