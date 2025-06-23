package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.EffectFuncEntry.Effect;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EffectFunc extends AbstractSimpleFunc {

    private final List<Effect> effects;

    public EffectFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, List<Effect> effects) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.effects = effects;
    }

    @Override
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        List<GamePlayer> playersToProcess = new ArrayList<>(gamePlayerList); // 遍历副本，不然玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            if (spatialZone.isWithinZone(gamePlayer.getLastPos(), progress)) {
                if (gamePlayer.isActiveEntity()) {
                    LivingEntity entity = (LivingEntity) serverLevel.getEntity(gamePlayer.getPlayerUUID());
                    if (entity != null && entity.isAlive()) {
                        for (Effect effect : effects) {
                            MobEffectInstance effectInstance = new MobEffectInstance(
                                    effect.type(),
                                    effect.duration(),
                                    effect.level(),
                                    false,
                                    false
                            );
                            entity.addEffect(effectInstance);
                        }
                        // success stats record
                    } else {
                        // failed stats record type 2 (unexpected
                    }
                } else {
                    // failed stats record type 1
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.EFFECT;
    }
}
