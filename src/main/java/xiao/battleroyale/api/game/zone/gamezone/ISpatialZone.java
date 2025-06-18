package xiao.battleroyale.api.game.zone.gamezone;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ISpatialZone {

    /**
     * 判断是否处于执行逻辑的区域范围内
     * 安全区和毒圈都是判断区域范围内，安全区的区域外扣血逻辑不是 ISpatialZone 的职责
     * @param checkPos 待检查的玩家/人机位置
     * @param progress 进度，用于确定计算所需的圈的状态
     * @return 判定结果
     */
    boolean isWithinZone(@Nullable Vec3 checkPos, double progress);

    /**
     * @return 当前圈形状
     */
    ZoneShapeType getShapeType();

    /**
     * 用于初始化起止状态
     * @param serverLevel 当前世界
     * @param standingGamePlayers 当前存活玩家列表
     * @param random 随机数生产者
     */
    void calculateShape(ServerLevel serverLevel, List<GamePlayer> standingGamePlayers, Supplier<Float> random);

    /**
     * 判断起止状态是否已经确定
     * @return 判定结果
     */
    boolean isDetermined();

    @Nullable
    Vec3 getStartCenterPos();

    /**
     * 特定进度的状态，基于 progress 的比例，得出从 start 到 end 之间的状态
     * @param progress 从 0 到 1 的比例
     * @return 中心点的3维坐标（二维用x,z）
     */
    @Nullable
    Vec3 getCenterPos(double progress);

    @Nullable
    Vec3 getEndCenterPos();

    @Nullable
    Vec3 getStartDimension();

    /**
     * 特定进度的状态，基于 progress 的比例，得出从 start 到 end 之间的状态
     * @param progress 从 0 到 1 的比例
     * @return 中心点的3维坐标（二维用x,z）
     */
    @Nullable
    Vec3 getDimension(double progress);

    @Nullable
    Vec3 getEndDimension();

    /**
     * 供多边形使用
     */
    default
    int getSegments() {
        return 3;
    }
}
