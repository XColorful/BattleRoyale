package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.EffectFuncEntry.Effect;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EffectFunc extends AbstractSimpleFunc {

    private final List<Effect> effects;

    public EffectFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, List<Effect> effects) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.effects = effects;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        Registry<MobEffect> mobEffectRegistry = zoneTickContext.serverLevel.registryAccess()
                .registryOrThrow(Registries.MOB_EFFECT);

        List<GamePlayer> playersToProcess = new ArrayList<>(zoneTickContext.gamePlayers); // 遍历副本，不然玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                if (gamePlayer.isActiveEntity()) {
                    LivingEntity entity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                    if (entity != null && entity.isAlive()) {
                        for (Effect effect : effects) {
                            Optional<Holder.Reference<MobEffect>> effectHolderOpt = mobEffectRegistry.getHolder(effect.effectRL());

                            if (effectHolderOpt.isEmpty()) {
                                BattleRoyale.LOGGER.error("MobEffect not found in registry: {}", effect.effectRL());
                                continue;
                            }
                            Holder<MobEffect> effectHolder = effectHolderOpt.get();

                            MobEffectInstance effectInstance = new MobEffectInstance(
                                    effectHolder,
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
