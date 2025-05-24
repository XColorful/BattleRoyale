package xiao.battleroyale.api.game.zone.gamezone;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ITickableZone {

    /**
     * 用于初始化起止状态
     * @param serverLevel 当前世界
     * @param gamePlayers 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例
     * @param random 随机数生产者
     */
    void initFunc(ServerLevel serverLevel, List<LivingEntity> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random);

    /**
     * 判断是否可以执行tick
     * @return 判定结果
     */
    boolean isReady();

    /**
     * tick当前圈的功能
     * @param serverLevel 当前世界
     * @param gamePlayers 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例，但通常圈自身逻辑与其他圈无关
     * @param random 随机数生产者
     * @param gameTime 游戏进行时间
     */
    void tick(ServerLevel serverLevel, List<LivingEntity> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime);

    /**
     * @return 当前圈功能类型
     */
    ZoneFuncType getFuncType();

    double getDamage();

    /**
     * 结合自身管理的 moveDelay, moveTime 得出当前圈的进度
     * @param currentGameTime 当前游戏进行的时间
     * @param zoneDelay 圈生成延迟
     * @return [0,1] 的进度比例
     */
    double getShapeProgress(int currentGameTime, int zoneDelay);
}
