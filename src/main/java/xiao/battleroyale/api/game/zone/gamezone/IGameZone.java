package xiao.battleroyale.api.game.zone.gamezone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 游戏进行时实际用于计算的对象
 */
public interface IGameZone extends ITickableZone, ISpatialZone{

    /**
     * 用于与当前游戏进行时间比较
     * 游戏运行时长小于 zoneDelay 则不tick其功能
     * @return 当前圈生成的延迟 (tick)
     */
    int getZoneDelay();

    /**
     * 为初始化 ITickableZone 和 ISpatialZone 传递参数
     * @param serverLevel 当前世界
     * @param gamePlayers 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例
     * @param random 随机数生产者
     */
    void createZone(ServerLevel serverLevel, List<LivingEntity> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random);

    /**
     * 是否初始化成功，判断是否创建以及是否可以执行
     * @return 判定结果
     */
    boolean isCreated();

    /**
     * 是否正在执行阶段
     * @return 判定结果
     */
    boolean isPresent();

    /**
     * 是否当前圈已经执行完成
     * @return 判定结果
     */
    boolean isFinished();

    /**
     * 服务端向客户端通信的序列化
     * @param progress 当前圈进度
     * @return 序列化NBT
     */
    CompoundTag toNBT(double progress);
}