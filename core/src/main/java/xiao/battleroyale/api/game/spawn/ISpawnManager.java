package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;

public interface ISpawnManager extends IGameSubManager {

    /**
     * 该方法仅用于提示SpawnManager需要持有IGameSpawner
     * 如需调用则应仅用于调试目的
     */
    @Deprecated(forRemoval = false)
    IGameSpawner getGameSpawner();

    /**
     * 在游戏中添加再出生的玩家
     */
    void respawn(List<GamePlayer> respawnGamePlayers);
}
