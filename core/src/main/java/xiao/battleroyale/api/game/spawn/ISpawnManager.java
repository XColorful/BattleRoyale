package xiao.battleroyale.api.game.spawn;

import xiao.battleroyale.api.game.IGameSubManager;

public interface ISpawnManager extends IGameSubManager {

    /**
     * 该方法仅用于提示SpawnManager需要持有IGameSpawner
     * 如需调用则应仅用于调试目的
     */
    @Deprecated(forRemoval = false)
    IGameSpawner getGameSpawner();
}
