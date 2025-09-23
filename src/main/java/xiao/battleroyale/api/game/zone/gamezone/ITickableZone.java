package xiao.battleroyale.api.game.zone.gamezone;

import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public interface ITickableZone {

    /**
     * 用于初始化起止状态
     */
    void initFunc(ZoneContext zoneContext);

    /**
     * 判断是否可以执行tick
     * @return 判定结果
     */
    boolean isReady();

    /**
     * 用于节约不必要的开销
     * @return funcTick 功能的频率
     */
    int getTickFrequency();

    void setTickFrequency(int tickFreq);

    /**
     * 支持错峰 funcTick 实现分摊
     * @return funcTick 的时间偏移
     */
    int getTickOffset();

    void setTickOffset(int tickOffset);

    /**
     * tick当前圈的功能
     */
    void funcTick(ZoneTickContext zoneTickContext);

    /**
     * @return 当前圈功能类型
     */
    ZoneFuncType getFuncType();

    /**
     * 结合自身管理的 moveDelay, moveTime 得出当前圈的进度
     * @param currentGameTime 当前游戏进行的时间
     * @param zoneDelay 圈生成延迟
     * @return [0,1] 的进度比例
     */
    double getShapeProgress(int currentGameTime, int zoneDelay);

    int getShapeMoveDelay();
    int getShapeMoveTime();
}
