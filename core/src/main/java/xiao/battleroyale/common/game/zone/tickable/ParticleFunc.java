package xiao.battleroyale.common.game.zone.tickable;

import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;

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
        if (particleIdList.isEmpty()) {
            return;
        }

        int size = particleIdList.size();
        if (skipRandom) { // 只生成一个唯一粒子
            for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
                if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                    EffectManager.get().addParticle(zoneTickContext.serverLevel, gamePlayer.getPlayerUUID(), channel, particleIdList.get(0), cooldown);
                }
            }
        } else { // 多个粒子，最后一个粒子添加通道冷却
            for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
                if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                    // 第一个粒子检测是否在冷却
                    int selected = (int) (size * zoneTickContext.random.get());
                    if (!EffectManager.get().addParticle(zoneTickContext.serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0)) {
                        continue;
                    }

                    for (int i = 1; i < this.select - 1; i++) {
                        selected = (int) (size * zoneTickContext.random.get());
                        EffectManager.get().addParticle(zoneTickContext.serverLevel, gamePlayer.getPlayerUUID(), channel, selected, 0);
                    }
                    // 最后一个粒子添加冷却
                    selected = (int) (size * zoneTickContext.random.get());
                    EffectManager.get().addParticle(zoneTickContext.serverLevel, gamePlayer.getPlayerUUID(), channel, selected, cooldown);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.PARTICLE;
    }
}
