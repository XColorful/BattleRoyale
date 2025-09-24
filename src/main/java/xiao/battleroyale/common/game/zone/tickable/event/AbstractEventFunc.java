package xiao.battleroyale.common.game.zone.tickable.event;

import xiao.battleroyale.common.game.zone.tickable.AbstractSimpleFunc;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public abstract class AbstractEventFunc extends AbstractSimpleFunc {

    public AbstractEventFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.EVENT;
    }
}
