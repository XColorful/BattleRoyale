package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.ArrayList;
import java.util.List;

public class FireworkFunc extends AbstractSimpleFunc {

    public final boolean trackPlayer;
    public final int amount;
    public final int interval;
    public final int vRange;
    public final int hRange;
    public final boolean outside;

    public FireworkFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                        boolean trackPlayer, int amount, int interval, int vRange, int hRange, boolean outside) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.trackPlayer = trackPlayer;
        this.amount = amount;
        this.interval = interval;
        this.vRange = vRange;
        this.hRange = hRange;
        this.outside = outside;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        List<GamePlayer> playersToProcess = new ArrayList<>(zoneTickContext.gamePlayers); // 遍历副本，不然烟花碰到玩家玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            boolean isWithinZone = zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress);
            if (isWithinZone != outside) {
                if (trackPlayer) {
                    @Nullable ServerPlayer player = zoneTickContext.serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                    if (player != null) {
                        EffectManager.get().spawnPlayerFirework(player, amount, interval, vRange, hRange);
                    }
                } else {
                    EffectManager.get().spawnFirework(zoneTickContext.serverLevel, gamePlayer.getLastPos(), amount, interval, vRange, hRange);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.FIREWORK;
    }
}
