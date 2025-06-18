package xiao.battleroyale.config.common.game.zone.zonefunc;

import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;

public abstract class AbstractFuncEntry implements IZoneFuncEntry {

    protected final double damage;
    protected final int moveDelay;
    protected final int moveTime;

    public AbstractFuncEntry(int moveDelay, int moveTime) {
        this(0, moveDelay, moveTime);
    }

    public AbstractFuncEntry(double damage, int moveDelay, int moveTime) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }
}
