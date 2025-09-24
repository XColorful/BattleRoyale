package xiao.battleroyale.config.common.game.zone.zonefunc.event;

import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.config.common.game.zone.zonefunc.AbstractFuncEntry;

public abstract class AbstractEventFuncEntry extends AbstractFuncEntry {

    public AbstractEventFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset) {
        super(moveDelay, moveTime, funcFreq, funcOffset);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.EVENT;
    }
}
