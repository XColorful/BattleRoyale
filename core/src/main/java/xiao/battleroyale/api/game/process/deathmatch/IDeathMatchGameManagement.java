package xiao.battleroyale.api.game.process.deathmatch;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.process.IGameManagement;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface IDeathMatchGameManagement extends IGameManagement {

    default void checkAndUpdateGamePlayerPre(ServerLevel serverLevel) {
        checkAndUpdateRestandingGamePlayer(serverLevel);
    }
    void checkAndUpdateRestandingGamePlayer(ServerLevel serverLevel);

    boolean respawnGamePlayer(ServerLevel serverLevel, GamePlayer gamePlayer);
}
