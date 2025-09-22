package xiao.battleroyale.common.game;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;

import java.util.List;
import java.util.UUID;

/**
 * 将GameManager集中的接口抽离成static方法
 */
public class GameTeamManager {
    
    private static final IGameTeamReadApi teamManagerInstance = TeamManager.get();

    // TeamManager
    public static int getPlayerLimit() { return teamManagerInstance.getPlayerLimit(); }
    public static @Nullable GamePlayer getGamePlayerByUUID(UUID uuid) { return teamManagerInstance.getGamePlayerByUUID(uuid); }
    public static @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return teamManagerInstance.getGamePlayerBySingleId(playerId); }
    public static boolean hasStandingGamePlayer(UUID uuid) { return teamManagerInstance.hasStandingGamePlayer(uuid);}
    public static List<GameTeam> getGameTeams() { return teamManagerInstance.getGameTeams(); }
    public static @Nullable GameTeam getGameTeamById(int teamId) { return teamManagerInstance.getGameTeamById(teamId); }
    public static List<GamePlayer> getGamePlayers() { return teamManagerInstance.getGamePlayers(); }
    public static List<GamePlayer> getStandingGamePlayers() { return teamManagerInstance.getStandingGamePlayers(); }
    public static @Nullable GamePlayer getRandomStandingGamePlayer() { return teamManagerInstance.getRandomStandingGamePlayer(); }

    public static IGameTeamReadApi getApi() {
        return teamManagerInstance;
    }
}
