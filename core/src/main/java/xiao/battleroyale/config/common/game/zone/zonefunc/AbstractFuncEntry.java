package xiao.battleroyale.config.common.game.zone.zonefunc;

import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;

public abstract class AbstractFuncEntry implements IZoneFuncEntry {

    protected final int moveDelay;
    protected final int moveTime;
    protected final int tickFreq;
    protected final int tickOffset;

    public AbstractFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset) {
        this.moveDelay = Math.max(moveDelay, 0);
        this.moveTime = Math.max(moveTime, 0);
        this.tickFreq = Math.max(funcFreq, 1);
        this.tickOffset = Math.max(funcOffset, -1);
    }
}
