package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;

public abstract class AbstractSimpleFunc implements ITickableZone {

    protected int moveDelay;
    protected int moveTime;
    protected int tickFreq; // 不小于0
    protected int tickOffset; // [0, tickFreq - 1]

    protected boolean ready = false;

    public AbstractSimpleFunc(int moveDelay, int moveTime) {
        this(moveDelay, moveTime, 20, 0);
    }

    public AbstractSimpleFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
        setTickFrequency(tickFreq);
        this.tickOffset = tickOffset;
    }

    @Override
    public void initFunc(ZoneContext zoneContext) {
        this.ready = true;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) {
        currentGameTime -= zoneDelay + moveDelay;
        if (currentGameTime < 0) {
            return 0;
        }
        return moveTime == 0 ? 1 : Math.min((double)currentGameTime / moveTime, 1.0D);
    }

    @Override
    public int getShapeMoveDelay() { return this.moveDelay; }

    @Override
    public int getShapeMoveTime() { return this.moveTime; }

    @Override
    public int getTickFrequency() { return this.tickFreq; }

    @Override
    public int getTickOffset() { return this.tickOffset; }

    @Override
    public void setTickFrequency(int tickFreq) {
        this.tickFreq = Math.max(tickFreq, 1);
        if (this.tickOffset > this.tickFreq) {
            setTickOffset(tickFreq - 1);
        }
    }

    @Override
    public void setTickOffset(int tickOffset) {
        if (tickOffset < 0) {
            tickOffset = BattleRoyale.COMMON_RANDOM.nextInt(this.tickFreq + 1);
        }
        this.tickOffset = Math.min(Math.max(tickOffset, 0), this.tickFreq - 1);
    }
}
