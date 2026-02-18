package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IGameSpawner {

    /**
     * 预处理点位
     * @param random 随机数生产者
     * @param spawnPointsTotal 提供的点位数量
     */
    void init(Supplier<Float> random, int spawnPointsTotal);

    /**
     * @return 是否可以tick
     */
    boolean isReady();

    /**
     * 一般以队伍为单位出生，因此不用 GamePlayer 列表
     * @param gameTime 当前游戏时间
     * @param gameTeams 当前存活的队伍列表
     */
    void spawnTick(int gameTime, List<GameTeam> gameTeams);

    @Deprecated default boolean shouldTick() {
        return !isSpawnTickComplete();
    }

    /**
     * 用于控制是否结束Spawn
     */
    boolean isSpawnTickComplete();

    /**
     * 清空已有信息
     */
    void clear();

    /**
     * 在初始出生后再次出生
     * 不一定是队伍全体玩家都需要再出生
     * @param gameTime 当前游戏时间
     * @param respawnTeams 再出生的玩家
     */
    void respawnTick(int gameTime, Map<GameTeam, List<GamePlayer>> respawnTeams);
}
