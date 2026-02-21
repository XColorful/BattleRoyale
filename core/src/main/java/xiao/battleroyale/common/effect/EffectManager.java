package xiao.battleroyale.common.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.effect.IEffectManager;
import xiao.battleroyale.api.effect.IEffectSubManager;
import xiao.battleroyale.api.effect.type.IBoostManager;
import xiao.battleroyale.api.effect.type.IFireworkManager;
import xiao.battleroyale.api.effect.type.IMutekiManager;
import xiao.battleroyale.api.effect.type.IParticleManager;
import xiao.battleroyale.common.effect.boost.BoostManager;
import xiao.battleroyale.common.effect.firework.FireworkManager;
import xiao.battleroyale.common.effect.muteki.MutekiManager;
import xiao.battleroyale.common.effect.particle.FixedParticleChannel;
import xiao.battleroyale.common.effect.particle.FixedParticleData;
import xiao.battleroyale.common.effect.particle.ParticleData;
import xiao.battleroyale.common.effect.particle.ParticleManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager;
import xiao.battleroyale.config.common.effect.particle.ParticleConfigManager.ParticleConfig;

import java.util.UUID;

public class EffectManager implements IEffectManager {

    private static class EffectManagerHolder {
        private static final EffectManager INSTANCE = new EffectManager();
    }

    public static EffectManager get() {
        return EffectManagerHolder.INSTANCE;
    }

    private EffectManager() {}

    public static void init(McSide mcSide) {
    }

    private IBoostManager boostManager = BoostManager.get();
    private IFireworkManager fireworkManager = FireworkManager.get();
    private IMutekiManager mutekiManager = MutekiManager.get();
    private IParticleManager particleManager = ParticleManager.get();

    protected void registerNewManager(IEffectSubManager previousManager, IEffectSubManager newManager) {
        // EffectSubManager 目前没有事件注册机制，如果有需要在这里添加
        BattleRoyale.LOGGER.debug("Register new EffectSubManager {} to effect manager", newManager.getClass().getSimpleName());
    }

    @Override
    public boolean setBoostManager(@NotNull IBoostManager boostManager) {
        registerNewManager(this.boostManager, boostManager);
        this.boostManager = boostManager;
        return true;
    }
    @Override
    public boolean setFireworkManager(@NotNull IFireworkManager fireworkManager) {
        registerNewManager(this.fireworkManager, fireworkManager);
        this.fireworkManager = fireworkManager;
        return true;
    }
    @Override
    public boolean setMutekiManager(@NotNull IMutekiManager mutekiManager) {
        registerNewManager(this.mutekiManager, mutekiManager);
        this.mutekiManager = mutekiManager;
        return true;
    }
    @Override
    public boolean setParticleManager(@NotNull IParticleManager particleManager) {
        registerNewManager(this.particleManager, particleManager);
        this.particleManager = particleManager;
        return true;
    }

    @Override public IBoostManager getBoostManager() { return boostManager; }
    @Override public IFireworkManager getFireworkManager() { return fireworkManager; }
    @Override public IMutekiManager getMutekiManager() { return mutekiManager; }
    @Override public IParticleManager getParticleManager() { return particleManager; }

    /**
     * 在特定位置生成垂直烟花
     * @param serverLevel 烟花所在level
     * @param pos 生成位置中心点
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    @Override
    public void spawnFirework(ServerLevel serverLevel, Vec3 pos, int amount, int interval, float vRange, float hRange) {
        if (serverLevel == null) {
            return;
        }
        fireworkManager.addFixedPositionFireworkTask(serverLevel, pos, amount, interval, vRange, hRange);
    }

    /**
     * 跟随玩家生成烟花
     * @param player 玩家
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    @Override
    public void spawnPlayerFirework(@Nullable LivingEntity player, int amount, int interval, float vRange, float hRange) {
        if (player == null) {
            return;
        }
        fireworkManager.addPlayerTrackingFireworkTask((ServerLevel) player.level(), player.getUUID(), amount, interval, vRange, hRange);
    }

    /**
     * 使指定实体免伤
     * @param serverLevel 生效维度
     * @param livingEntity 生效实体
     * @param duration 持续时间
     */
    // 仅GameZone调用
    @Override
    public void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration) {
        mutekiManager.addMutekiEntity(serverLevel, livingEntity, duration);
    }
    // 仅指令调用
    @Override
    public void addMutekiPlayer(ServerLevel serverLevel, ServerPlayer player, int duration) {
        mutekiManager.addMutekiEntityNotify(serverLevel, player, duration);
    }

    /**
     * 为指定实体添加能量条
     * @param entityUUID 实体UUID
     * @param boostAmount 能量
     * @param serverLevel 生效维度
     */
    @Override
    public void addBoost(UUID entityUUID, int boostAmount, ServerLevel serverLevel) {
        boostManager.addBoost(entityUUID, boostAmount, serverLevel);
    }
    @Override
    public int getBoost(UUID entityUUID) {
        return boostManager.getBoost(entityUUID);
    }

    /**
     * 添加粒子效果
     * @param serverLevel 粒子生效维度
     * @param channelKey 通道名称
     * @param particleId 粒子id
     * @param channelCooldown 通道冷却时间
     */
    // 仅ZoneFunc调用
    @Override
    public boolean addParticle(ServerLevel serverLevel, UUID entityUUID, String channelKey, int particleId, int channelCooldown) {
        IConfigManager effectConfigManager = BattleRoyale.getModConfigManager().getConfigManager(EffectConfigManager.get().getNameKey());
        if (effectConfigManager == null) return false;

        ParticleConfig particleConfig = effectConfigManager.getConfigEntry(ParticleConfigManager.get().getNameKey(), particleId) instanceof ParticleConfig config ? config : null;
        if (particleConfig != null) {
            ParticleData particleData = particleConfig.createParticleData(serverLevel);
            return particleManager.addEntityParticle(entityUUID, channelKey, particleData, channelCooldown);
        }
        return false;
    }
    // 仅玩家指令调用
    @Override
    public boolean addParticle(ServerLevel serverLevel, Vec3 spawnPos, String channelKey, int particleId, int channelCooldown) {
        IConfigManager effectConfigManager = BattleRoyale.getModConfigManager().getConfigManager(EffectConfigManager.get().getNameKey());
        if (effectConfigManager == null) return false;

        ParticleConfig particleConfig = effectConfigManager.getConfigEntry(ParticleConfigManager.get().getNameKey(), particleId) instanceof ParticleConfig config ? config : null;
        if (particleConfig != null) {
            FixedParticleData particleData = particleConfig.createParticleData(serverLevel, spawnPos);
            return particleManager.addFixedParticle(channelKey, particleData, channelCooldown);
        }
        return false;
    }
    // 仅GameManager调用
    @Override
    public boolean addGameParticle(ServerLevel serverLevel, Vec3 spawnPos, int particleId, int channelCooldown) {
        return addParticle(serverLevel, spawnPos, FixedParticleChannel.GAME_CHANNEL, particleId, channelCooldown);
    }
    // 仅非玩家指令调用
    @Override
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

    @Override
    public void clearFirework() {
        fireworkManager.clear();
    }

    @Override
    public void clearMuteki() {
        mutekiManager.clear();
    }
    @Override
    public boolean clearMuteki(UUID uuid) {
        return mutekiManager.clear(uuid);
    }

    @Override
    public void clearBoost() {
        boostManager.clear();
    }
    @Override
    public void clearBoost(UUID entityUUID) {
        boostManager.clear(entityUUID);
    }

    // 清除所有粒子
    @Override
    public void clearParticle() {
        particleManager.clear();
    }
    // 仅游戏区域调用
    @Override
    public void clearParticle(UUID entityUUID) {
        particleManager.clear(entityUUID);
    }
    @Override
    public void clearParticle(UUID entityUUID, String channelKey) {
        particleManager.clear(entityUUID, channelKey);
    }
    // 仅GameManager调用
    @Override
    public void clearGameParticle() {
        particleManager.clear(FixedParticleChannel.GAME_CHANNEL);
    }
    // 仅非玩家指令调用
    @Override
    public void clearCommandParticle() {
        particleManager.clear(FixedParticleChannel.COMMAND_CHANNEL);
    }

    @Override
    public void forceEnd() {
        fireworkManager.forceEnd();
        mutekiManager.forceEnd();
        particleManager.forceEnd();
        boostManager.forceEnd();
    }

    @Override
    public boolean shouldEnd() {
        return false;
    }
}
