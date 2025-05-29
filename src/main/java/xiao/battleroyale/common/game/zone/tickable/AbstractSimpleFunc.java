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
    protected int tickOff;

    protected boolean ready;

    public AbstractSimpleFunc(int moveDelay, int moveTime) {
        this(0, moveDelay, moveTime, 20, 0);
    }

    public AbstractSimpleFunc(int moveDelay, int moveTime, int tickFreq, int tickOff) {
        this(0, moveDelay, moveTime, tickFreq, tickOff);
    }

    public AbstractSimpleFunc(double damage, int moveDelay, int moveTime) {
        this(damage, moveDelay, moveTime, 20, 0);
    }

    public AbstractSimpleFunc(double damage, int moveDelay, int moveTime, int funcFreq, int tickOff) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
        this.funcFreq = funcFreq;
        this.tickOff = tickOff;
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {

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
    public int getFuncOffset() { return this.tickOff; }

    @Override
    public void setFuncFrequency(int funcFreq) { this.funcFreq = funcFreq; }

    @Override
    public void setFuncOffset(int funcOff) { this.tickOff = funcOff; }
}
