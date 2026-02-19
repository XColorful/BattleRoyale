package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.init.registry.ModDamageTypes;
import xiao.battleroyale.util.GameUtils;

import java.util.ArrayList;
import java.util.List;

public class SafeFunc extends AbstractDamageFunc {

    public SafeFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset, float damage) {
        super(moveDelay, moveTime, tickFreq, tickOffset, damage);
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        List<GamePlayer> playersToProcess = new ArrayList<>(zoneTickContext.gamePlayers); // 遍历副本，不然玩家挂了就 ConcurrentModificationException
        for (GamePlayer gamePlayer : playersToProcess) {
            if (!zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                playerFunc(zoneTickContext.serverLevel, gamePlayer);
            }
        }
    }
    @Override
    public void playerFunc(@NotNull ServerLevel serverLevel, GamePlayer gamePlayer) {
        if (gamePlayer.isActiveEntity()) { // 造成一次毒圈伤害
            @Nullable LivingEntity entity = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
            if (entity != null && entity.isAlive()) {
                entity.hurt(ModDamageTypes.safeZone(serverLevel), this.damage);
            }
        } else {
            // 暂时不记录
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.SAFE;
    }
}