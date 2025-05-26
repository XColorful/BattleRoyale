package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DangerFunc implements ITickableZone {

    private int moveDelay;
    private int moveTime;

    public DangerFunc(int moveDelay, int moveTime) {
        this.moveDelay = moveDelay;
        this.moveTime = moveTime;
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void tick(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {

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
