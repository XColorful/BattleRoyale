package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.effect.IEffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.function.Supplier;

public class ParticleFunc extends AbstractSimpleFunc {

    private final List<Integer> particleIdList;
    private final int select;
    private final String channel;
    private final int cooldown;
    private final boolean skipRandom;

    public ParticleFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, List<Integer> particleIdList, int select, String channel, int cooldown) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.particleIdList = particleIdList;
        this.select = select;
        this.channel = channel;
        this.cooldown = cooldown;
        this.skipRandom = this.particleIdList.size() == 1 && select == 1;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        IEffectManager effectManager = BattleRoyale.getEffectManager();
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                playerFuncInternal(zoneTickContext.serverLevel, gamePlayer, effectManager, zoneTickContext.random);
            }
        }
    }
    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
        IEffectManager effectManager = BattleRoyale.getEffectManager();
        Supplier<Float> random = BattleRoyale.COMMON_RANDOM::nextFloat;
        playerFuncInternal(serverLevel, gamePlayer, effectManager, random);
    }
    public void playerFuncInternal(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer, IEffectManager effectManager, Supplier<Float> random) {
        if (particleIdList.isEmpty()) {
            return;
        }

        int size = particleIdList.size();
        if (skipRandom) { // 只生成一个唯一粒子
            effectManager.addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, particleIdList.get(0), cooldown);
        } else { // 多个粒子，最后一个粒子添加通道冷却
            // 第一个粒子检测是否在冷却
            int selected = (int) (size * random.get());
            if (!effectManager.addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0)) {
                return;
            }

            for (int i = 1; i < this.select - 1; i++) {
                selected = (int) (size * random.get());
                effectManager.addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0);
            }
            // 最后一个粒子添加冷却
            selected = (int) (size * random.get());
            effectManager.addParticle(serverLevel, gamePlayer.getPlayerUUID(), channel, selected, cooldown);
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.PARTICLE;
    }
}
