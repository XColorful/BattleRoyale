package xiao.battleroyale.common.game.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.common.game.effect.boost.BoostManager;
import xiao.battleroyale.common.game.effect.firework.FireworkManager;
import xiao.battleroyale.common.game.effect.muteki.MutekiManager;
import xiao.battleroyale.common.game.effect.particle.ParticleManager;

import java.util.UUID;

public class EffectManager implements IEffectManager {

    private EffectManager() {}

    /**
     * 在特定位置生成垂直烟花
     * @param serverLevel 烟花所在level
     * @param pos 生成位置中心点
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    public void spawnFirework(ServerLevel serverLevel, Vec3 pos, int amount, int interval, float vRange, float hRange) {
        if (serverLevel == null) {
            return;
        }
        FireworkManager.get().addFixedPositionFireworkTask(serverLevel, pos, amount, interval, vRange, hRange);
    }

    /**
     * 跟随玩家生成烟花
     * @param player 玩家
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    public void spawnPlayerFirework(@Nullable ServerPlayer player, int amount, int interval, float vRange, float hRange) {
        if (player == null) {
            return;
        }
        FireworkManager.get().addPlayerTrackingFireworkTask((ServerLevel) player.level(), player.getUUID(), amount, interval, vRange, hRange);
    }

    /**
     * 使指定实体免伤
     * @param serverLevel 生效维度
     * @param livingEntity 生效实体
     * @param duration 持续时间
     */
    public void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration) {
        MutekiManager.get().addMutekiEntity(serverLevel, livingEntity, duration);
    }

    public void addMutekiPlayer(ServerLevel serverLevel, ServerPlayer player, int duration) {
        MutekiManager.get().addMutekiEntityNotify(serverLevel, player, duration);
    }

    public void addBoost(UUID entityUUID, int boostAmount, ServerLevel serverLevel) {
        BoostManager.get().addBoost(entityUUID, boostAmount, serverLevel);
    }

    public int getBoost(UUID entityUUID) {
        return BoostManager.get().getBoost(entityUUID);
    }

    @Override
    public void clear() {
        clearFirework();
        clearMuteki();
        clearBoost();
        clearParticle();
    }

    public void clearFirework() {
        FireworkManager.get().clear();
    }

    public void clearMuteki() {
        MutekiManager.get().clear();
    }
    public boolean clearMuteki(UUID uuid) {
        return MutekiManager.get().clear(uuid);
    }

    public void clearBoost() {
        BoostManager.get().clear();
    }
    public void clearBoost(UUID entityUUID) {
        BoostManager.get().clear(entityUUID);
    }

    public void clearParticle() {
        ParticleManager.get().clear();
    }

    @Override
    public void forceEnd() {
        FireworkManager.get().forceEnd();
        MutekiManager.get().forceEnd();
        ParticleManager.get().forceEnd();
        BoostManager.get().forceEnd();
    }

    @Override
    public boolean shouldEnd() {
        return false;
    }

    private static class EffectManagerHolder {
        private static final EffectManager INSTANCE = new EffectManager();
    }

    public static EffectManager get() {
        return EffectManagerHolder.INSTANCE;
    }
}
