package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SafeFunc implements ITickableZone {

    private double damage;
    private int moveDelay;
    private int moveTime;

    public SafeFunc(double damage, int moveDelay, int moveTime) {
        this.damage = damage;
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<UUID> playerIdList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void tick(ServerLevel serverLevel, List<UUID> playerIdList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {

    }

    @Override
    public ZoneFuncType getFuncType() {
        return null;
    }

    @Override
    public double getDamage() {
        return 0;
    }

    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) {
        return 0;
    }
}
