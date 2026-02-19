package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.effect.IEffectManager;
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
                playerFunc(zoneTickContext.serverLevel, gamePlayer);
            }
        }
    }
    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
        IEffectManager effectManager = BattleRoyale.getEffectManager();
        @Nullable LivingEntity livingEntity = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
        if (livingEntity != null) {
            effectManager.addMutekiEntity(serverLevel, livingEntity, mutekiTime);
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.MUTEKI;
    }
}