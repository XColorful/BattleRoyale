package xiao.battleroyale.api.game.process.deathmatch;

import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

public interface IDeathMatchInfoGetter {

    int getCurrentMaxKill();

    Set<GameTeam> getTrackedGameTeams();
    Set<GamePlayer> getTrackedGamePlayers();

    Map<GameTeam, Integer> copyGameTeamKills();
    Map<GamePlayer, Integer> copyGamePlayerKills();

    /**
     * 获取按击杀数降序排列的队伍数据
     * 返回的 Map 键为击杀数，值为拥有该击杀数的队伍集合
     */
    NavigableMap<Integer, Set<GameTeam>> getTeamKillsInvertedSorted();
    /**
     * 获取按击杀数降序排列的玩家数据
     * 返回的 Map 键为击杀数，值为拥有该击杀数的玩家集合
     */
    NavigableMap<Integer, Set<GamePlayer>> getPlayerKillsInvertedSorted();

    /**
     * 获取击杀数大于或等于指定值的队伍 Map 副本
     */
    NavigableMap<Integer, Set<GameTeam>> getTeamKillsGreaterOrEqual(int minKills);

    /**
     * 获取击杀数大于或等于指定值的玩家 Map 副本
     */
    NavigableMap<Integer, Set<GamePlayer>> getPlayerKillsGreaterOrEqual(int minKills);
}
