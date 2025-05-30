package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractSimpleFunc implements ITickableZone {

    protected double damage;
    protected int moveDelay;
    protected int moveTime;

    protected int funcFreq;
    protected int funcOff;

    protected boolean ready = false;

    public AbstractSimpleFunc(int moveDelay, int moveTime) {
        this(0, moveDelay, moveTime, 20, 0);
    }

    public AbstractSimpleFunc(int moveDelay, int moveTime, int tickFreq, int funcOff) {
        this(0, moveDelay, moveTime, tickFreq, funcOff);
    }

    public AbstractSimpleFunc(double damage, int moveDelay, int moveTime) {
        this(damage, moveDelay, moveTime, 20, 0);
    }

    public AbstractSimpleFunc(double damage, int moveDelay, int moveTime, int funcFreq, int funcOff) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
        setFuncFrequency(funcFreq);
        setFuncOffset(funcOff);
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        this.ready = true;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) {
        currentGameTime -= zoneDelay + moveDelay;
        if (currentGameTime < 0) {
            return 0;
        }
        return Math.min((double)currentGameTime / moveTime, 1.0D);
    }

    @Override
    public int getFuncFrequency() { return this.funcFreq; }

    @Override
    public int getFuncOffset() { return this.funcOff; }

    @Override
    public void setFuncFrequency(int funcFreq) {
        this.funcFreq = Math.max(funcFreq, 1);
        if (this.funcOff > this.funcFreq) {
            this.funcOff = this.funcFreq;
        }
    }

    @Override
    public void setFuncOffset(int funcOff) {
        this.funcOff = Math.min(Math.max(funcOff, 0), this.funcFreq);
    }
}
