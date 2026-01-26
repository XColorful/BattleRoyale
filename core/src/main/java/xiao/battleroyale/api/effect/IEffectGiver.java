package xiao.battleroyale.api.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IEffectGiver {

    /**
     * 在特定位置生成垂直烟花
     * @param serverLevel 烟花所在level
     * @param pos 生成位置中心点
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    void spawnFirework(ServerLevel serverLevel, Vec3 pos, int amount, int interval, float vRange, float hRange);

    /**
     * 跟随玩家生成烟花
     * @param player 玩家
     * @param amount 总生成数量
     * @param interval 每个烟花的时间间隔
     * @param vRange 使中心点往上随机偏移
     * @param hRange 水平偏移半径
     */
    void spawnPlayerFirework(@Nullable ServerPlayer player, int amount, int interval, float vRange, float hRange);

    /**
     * 使指定实体免伤
     * @param serverLevel 生效维度
     * @param livingEntity 生效实体
     * @param duration 持续时间
     */
    // 仅GameZone调用
    void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration);
    // 仅指令调用
    void addMutekiPlayer(ServerLevel serverLevel, ServerPlayer player, int duration);

    /**
     * 为指定实体添加能量条
     * @param entityUUID 实体UUID
     * @param boostAmount 能量
     * @param serverLevel 生效维度
     */
    void addBoost(UUID entityUUID, int boostAmount, ServerLevel serverLevel);

    /**
     * 添加粒子效果
     * @param serverLevel 粒子生效维度
     * @param channelKey 通道名称
     * @param particleId 粒子id
     * @param channelCooldown 通道冷却时间
     */
    // 仅ZoneFunc调用
    boolean addParticle(ServerLevel serverLevel, UUID entityUUID, String channelKey, int particleId, int channelCooldown);
    // 仅玩家指令调用
    boolean addParticle(ServerLevel serverLevel, Vec3 spawnPos, String channelKey, int particleId, int channelCooldown);
    // 仅GameManager调用
    boolean addGameParticle(ServerLevel serverLevel, Vec3 spawnPos, int particleId, int channelCooldown);
    // 仅非玩家指令调用
    boolean addCommandParticle(ServerLevel serverLevel, Vec3 spawnPos, int particleId, int channelCooldown);
}
