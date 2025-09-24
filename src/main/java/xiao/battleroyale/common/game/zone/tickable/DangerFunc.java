package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class DangerFunc extends AbstractSimpleFunc {

    public DangerFunc(int moveDelay, int moveTime) {
        super(moveDelay, moveTime);
    }

    @Override
    public void initFunc(ZoneContext zoneContext) {
        // TODO 预处理
        this.ready = true;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
    }

    @Override
    public ZoneFuncType getFuncType() {
        return null;
    }

    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) {
        return 0;
    }
}
