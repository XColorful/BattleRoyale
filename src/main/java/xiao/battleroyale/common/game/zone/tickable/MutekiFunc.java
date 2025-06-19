package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MutekiFunc extends AbstractSimpleFunc {

    private final int mutekiTime;

    public MutekiFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, int mutekiTime) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.mutekiTime = mutekiTime;
    }

    @Override
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        for (GamePlayer gamePlayer : gamePlayerList) {
            if (spatialZone.isWithinZone(gamePlayer.getLastPos(), progress)) {
                LivingEntity livingEntity = (LivingEntity) serverLevel.getEntity(gamePlayer.getPlayerUUID());
                if (livingEntity != null) {
                    EffectManager.get().addMutekiEntity(serverLevel, livingEntity, gameTime);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.MUTEKI;
    }
}