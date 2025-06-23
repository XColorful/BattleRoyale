package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BoostFunc extends AbstractSimpleFunc {

    public final int boost;

    public BoostFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, int boost) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.boost = boost;
    }

    @Override
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        for (GamePlayer gamePlayer : gamePlayerList) {
            if (spatialZone.isWithinZone(gamePlayer.getLastPos(), progress)) {
                EffectManager.get().addBoost(gamePlayer.getPlayerUUID(), boost, serverLevel);
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.BOOST;
    }
}