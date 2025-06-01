package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.init.ModDamageTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SafeFunc extends AbstractSimpleFunc {

    public SafeFunc(double damage, int moveDelay, int moveTime) {
        super(damage, moveDelay, moveTime);
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        this.ready = true;
    }

    @Override
    public void tick(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        List<GamePlayer> playersToProcess = new ArrayList<>(gamePlayerList); // 遍历副本，不然玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            if (!spatialZone.isWithinZone(gamePlayer.getLastPos(), progress)) {
                if (gamePlayer.isActiveEntity()) { // 造成一次毒圈伤害
                    LivingEntity entity = (LivingEntity) serverLevel.getEntity(gamePlayer.getPlayerUUID());
                    if (entity != null && entity.isAlive()) {
                        entity.hurt(ModDamageTypes.safeZone(serverLevel), (float) this.damage);
                    }
                } else {
                    gamePlayer.addZoneDamageTaken((float) this.damage);
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.SAFE;
    }
}
