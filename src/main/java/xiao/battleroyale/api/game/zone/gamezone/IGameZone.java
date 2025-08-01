package xiao.battleroyale.api.game.zone.gamezone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 游戏进行时实际用于计算的对象
 */
public interface IGameZone extends ITickableZone, ISpatialZone{

    /**
     * 用 List<ZoneConfig> 创建 <Integer, IGameZone> 时保留获取正确id的方式
     * @return zoneId
     */
    int getZoneId();

    /**
     * 将自身区域延迟加上先前区域延迟
     * 若返回 zoneId 大于-1，则尝试叠加
     * @return 选定的 zondId
     */
    int previousZoneDelayId();
    void setZoneDelay(int zoneDelay);

    /**
     * 用于与当前游戏进行时间比较
     * 游戏运行时长小于 zoneDelay 则不tick其功能
     * @return 当前圈生成的延迟 (tick)
     */
    int getZoneDelay();

    /**
     * @return 当前圈的配置名称
     */
    String getZoneName();

    /**
     * @return 当前圈的固定颜色
     */
    String getZoneColor();

    /**
     * 为初始化 ITickableZone 和 ISpatialZone 传递参数
     * @param serverLevel 当前世界
     * @param standingGamePlayers 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例
     * @param random 随机数生产者
     */
    void createZone(ServerLevel serverLevel, List<GamePlayer> standingGamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random);

    /**
     * tick当前圈的功能
     * @param serverLevel 当前世界
     * @param gamePlayerList 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例，但通常圈自身逻辑与其他圈无关
     * @param random 随机数生产者
     * @param gameTime 游戏进行时间
     */
    void tick(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime);


    /**
     * 是否初始化成功，判断是否创建以及是否可以执行
     * @return 判定结果
     */
    boolean isCreated();

    /**
     * 是否正在执行阶段，实际没什么用，但仍然保留
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