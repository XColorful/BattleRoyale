package xiao.battleroyale.api.game.process;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.Set;

public interface IGameManagement {

    /**
     * 检查所有传入的游戏玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel);

    /**
     * 传送玩家至大厅，如果正在游戏中则淘汰
     * @param player 需传送的玩家
     */
    void teleportToLobbyInGame(ServerPlayer player);

    void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,
                           boolean teleportWinnerAfterGame, boolean teleportAfterGame);

    boolean spectateGame(ServerPlayer player);

    void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers);
}
