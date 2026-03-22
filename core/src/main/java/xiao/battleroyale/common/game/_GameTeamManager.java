package xiao.battleroyale.common.game;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.UUID;

/**
 * 将GameManager集中的接口抽离成static方法
 */
public class _GameTeamManager {

    // TeamManager
    public static int getPlayerLimit() { return BattleRoyale.getGameManager().getTeamManager().getPlayerLimit(); }
    public static @Nullable GamePlayer getGamePlayerByUUID(UUID uuid) { return BattleRoyale.getGameManager().getTeamManager().getGamePlayerByUUID(uuid); }
    public static @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return BattleRoyale.getGameManager().getTeamManager().getGamePlayerBySingleId(playerId); }
    public static boolean hasStandingGamePlayer(UUID uuid) { return BattleRoyale.getGameManager().getTeamManager().hasStandingGamePlayer(uuid);}
    public static List<GameTeam> getGameTeams() { return BattleRoyale.getGameManager().getTeamManager().getGameTeams(); }
    public static @Nullable GameTeam getGameTeamById(int teamId) { return BattleRoyale.getGameManager().getTeamManager().getGameTeamById(teamId); }
    public static List<GamePlayer> getGamePlayers() { return BattleRoyale.getGameManager().getTeamManager().getGamePlayers(); }
    public static List<GamePlayer> getStandingGamePlayers() { return BattleRoyale.getGameManager().getTeamManager().getStandingGamePlayers(); }
    public static @Nullable GamePlayer getRandomStandingGamePlayer() { return BattleRoyale.getGameManager().getTeamManager().getRandomStandingGamePlayer(); }
}
