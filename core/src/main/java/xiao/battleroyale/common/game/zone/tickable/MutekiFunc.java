package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.util.GameUtils;

public class MutekiFunc extends AbstractSimpleFunc {

    private final int mutekiTime;

    public MutekiFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, int mutekiTime) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.mutekiTime = mutekiTime;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                @Nullable LivingEntity livingEntity = GameUtils.getLivingEntity(zoneTickContext.serverLevel, gamePlayer.getPlayerUUID());
                if (livingEntity != null) {
                    EffectManager.get().addMutekiEntity(zoneTickContext.serverLevel, livingEntity, mutekiTime);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.MUTEKI;
    }
}