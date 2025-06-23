package xiao.battleroyale.util;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;

public class GameUtils {

    /**
     * 计算给定玩家列表中所有玩家的平均中心点
     * 如果玩家列表为空，则返回 Vec3.ZERO
     * @param gamePlayers 游戏玩家列表
     * @return 所有玩家的平均中心点
     */
    @NotNull
    public static Vec3 calculatePlayerCenter(List<GamePlayer> gamePlayers) {
        if (gamePlayers.isEmpty()) {
            return Vec3.ZERO;
        }

        int n = gamePlayers.size();
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;

        for (GamePlayer gamePlayer : gamePlayers) {
            Vec3 pos = gamePlayer.getLastPos();
            sumX += pos.x;
            sumY += pos.y;
            sumZ += pos.z;
        }

        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }

    /**
     * 计算玩家平均中心点作为终点，线性插值
     * @param startVec 起始点
     * @param gamePlayers 游戏玩家列表
     * @param delta 插值因子
     * @return 插值后的 Vec3
     */
    public static Vec3 calculateCenterAndLerp(Vec3 startVec, List<GamePlayer> gamePlayers, double delta) {
        Vec3 playerCenter = calculatePlayerCenter(gamePlayers);
        return Vec3Utils.lerp(startVec, playerCenter, delta);
    }
}
