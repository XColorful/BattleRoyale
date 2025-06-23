package xiao.battleroyale.common.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.common.effect.boost.BoostManager;
import xiao.battleroyale.common.effect.firework.FireworkManager;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.common.effect.particle.FixedParticleChannel;
import xiao.battleroyale.common.effect.particle.FixedParticleData;
import xiao.battleroyale.common.effect.particle.ParticleData;
import xiao.battleroyale.common.effect.particle.ParticleManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager.ParticleConfig;

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
    // 仅GameZone调用
    public void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration) {
        MutekiManager.get().addMutekiEntity(serverLevel, livingEntity, duration);
    }
    // 仅指令调用
    public void addMutekiPlayer(ServerLevel serverLevel, ServerPlayer player, int duration) {
        MutekiManager.get().addMutekiEntityNotify(serverLevel, player, duration);
    }

    /**
     * 为指定实体添加能量条
     * @param entityUUID 实体UUID
     * @param boostAmount 能量
     * @param serverLevel 生效维度
     */
    public void addBoost(UUID entityUUID, int boostAmount, ServerLevel serverLevel) {
        BoostManager.get().addBoost(entityUUID, boostAmount, serverLevel);
    }
    public int getBoost(UUID entityUUID) {
        return BoostManager.get().getBoost(entityUUID);
    }

    /**
     * 添加粒子效果
     * @param serverLevel 粒子生效维度
     * @param channelKey 通道名称
     * @param particleId 粒子id
     * @param channelCooldown 通道冷却时间
     */
    // 仅ZoneFunc调用
    public boolean addParticle(ServerLevel serverLevel, UUID entityUUID, String channelKey, int particleId, int channelCooldown) {
        ParticleConfig particleConfig = EffectConfigManager.get().getParticleConfig(particleId);
        if (particleConfig != null) {
            ParticleData particleData = particleConfig.createParticleData(serverLevel);
            return ParticleManager.get().addEntityParticle(entityUUID, channelKey, particleData, channelCooldown);
        }
        return false;
    }
    // 仅玩家指令调用
    public boolean addParticle(ServerLevel serverLevel, Vec3 spawnPos, String channelKey, int particleId, int channelCooldown) {
        ParticleConfig particleConfig = EffectConfigManager.get().getParticleConfig(particleId);
        if (particleConfig != null) {
            FixedParticleData particleData = particleConfig.createParticleData(serverLevel, spawnPos);
            return ParticleManager.get().addFixedParticle(channelKey, particleData, channelCooldown);
        }
        return false;
    }
    // 仅GameManager调用
    public boolean addGameParticle(ServerLevel serverLevel, Vec3 spawnPos, int particleId, int channelCooldown) {
        return addParticle(serverLevel, spawnPos, FixedParticleChannel.GAME_CHANNEL, particleId, channelCooldown);
    }
    // 仅非玩家指令调用
    public boolean addCommandParticle(ServerLevel serverLevel, Vec3 spawnPos, int particleId, int channelCooldown) {
        return addParticle(serverLevel, spawnPos, FixedParticleChannel.COMMAND_CHANNEL, particleId, channelCooldown);
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

    // 清除所有粒子
    public void clearParticle() {
        ParticleManager.get().clear();
    }
    // 仅游戏区域调用
    public void clearParticle(UUID entityUUID) {
        ParticleManager.get().clear(entityUUID);
    }
    public void clearParticle(UUID entityUUID, String channelKey) {
        ParticleManager.get().clear(entityUUID, channelKey);
    }
    // 仅GameManager调用
    public void clearGameParticle() {
        ParticleManager.get().clear(FixedParticleChannel.GAME_CHANNEL);
    }
    // 仅非玩家指令调用
    public void clearCommandParticle() {
        ParticleManager.get().clear(FixedParticleChannel.COMMAND_CHANNEL);
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
