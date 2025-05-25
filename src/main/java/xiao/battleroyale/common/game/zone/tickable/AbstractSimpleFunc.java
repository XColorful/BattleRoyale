package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractSimpleFunc implements ITickableZone {

    protected double damage;
    protected int moveDelay;
    protected int moveTime;

    protected boolean ready;

    public AbstractSimpleFunc(int moveDelay, int moveTime) {
        this.damage = 0;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    public AbstractSimpleFunc(double damage, int moveDelay, int moveTime) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<UUID> playerIdList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {

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
}
