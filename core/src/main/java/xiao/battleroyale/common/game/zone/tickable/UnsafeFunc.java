package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.init.registry.ModDamageTypes;

import java.util.ArrayList;
import java.util.List;

public class UnsafeFunc extends AbstractDamageFunc {

    public UnsafeFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, float damage) {
        super(moveDelay, moveTime, tickFreq, tickOffset, damage);
    }

    @Override
    public void funcTick(ZoneManager.ZoneTickContext zoneTickContext) {
        List<GamePlayer> playersToProcess = new ArrayList<>(zoneTickContext.gamePlayers); // 遍历副本，不然玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                if (gamePlayer.isActiveEntity()) { // 造成一次毒圈伤害
                    LivingEntity entity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                    if (entity != null && entity.isAlive()) {
                        entity.hurt(ModDamageTypes.unsafeZone(zoneTickContext.serverLevel), this.damage);
                    }
                } else {
                    StatsManager.get().onRecordDamage(gamePlayer, ModDamageTypes.unsafeZone(zoneTickContext.serverLevel), (float) this.damage);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.UNSAFE;
    }
}
