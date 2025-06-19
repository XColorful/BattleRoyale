package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FireworkFunc extends AbstractSimpleFunc {

    private final boolean trackPlayer;
    private final int amount;
    private final int interval;
    private final int vRange;
    private final int hRange;
    private final boolean outside;

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
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        List<GamePlayer> playersToProcess = new ArrayList<>(gamePlayerList); // 遍历副本，不然烟花碰到玩家玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            boolean isWithinZone = spatialZone.isWithinZone(gamePlayer.getLastPos(), progress);
            if (isWithinZone != outside) {
                if (trackPlayer) {
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player != null) {
                        EffectManager.get().spawnPlayerFirework(player, amount, interval, vRange, hRange);
                    }
                } else {
                    EffectManager.get().spawnFirework(serverLevel, gamePlayer.getLastPos(), amount, interval, vRange, hRange);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.FIREWORK;
    }
}
