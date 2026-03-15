package xiao.battleroyale.api.game.process.deathmatch;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.process.IGameManagement;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface IDeathMatchGameManagement extends IGameManagement {

    default void checkAndUpdateGamePlayerPre(ServerLevel serverLevel) {
        checkAndUpdateRestandingGamePlayer(serverLevel);
    }
    void checkAndUpdateRestandingGamePlayer(ServerLevel serverLevel);

    /**
     * 将 GamePlayer 状态恢复 (属于 IGameProcessManager 管理)
     * 内部调用后通知 ISpawnManager 再出生 {@link xiao.battleroyale.api.game.spawn.ISpawnManager#respawn}
     */
    boolean respawnGamePlayer(ServerLevel serverLevel, GamePlayer gamePlayer);
}
