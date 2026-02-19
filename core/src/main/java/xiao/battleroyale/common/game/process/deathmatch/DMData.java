package xiao.battleroyale.common.game.process.deathmatch;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchDataManagement;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.common.game.AbstractGameManagerData;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.*;
import java.util.stream.Collectors;

public class DMData extends AbstractGameManagerData implements IDeathMatchDataManagement {

    private static final String DATA_NAME = "DeathMatchData";

    // 击杀数 (胜利条件)
    private final @NotNull Map<GameTeam, Integer> gameTeamKill;
    private final @NotNull Map<GamePlayer, Integer> gamePlayerKill;
    private int currentMaxKill = -1;
    // 缓存字段
    private boolean teamDirty = true;
    private boolean playerDirty = true;
    private NavigableMap<Integer, Set<GameTeam>> cacheTeamInverted;
    private NavigableMap<Integer, Set<GamePlayer>> cachePlayerInverted;

    // 待复活玩家
    private final Set<GamePlayer> trackedRestandingGamePlayers;
    private final Map<GamePlayer, Integer> queuedRestandingGamePlayer;
    private int trackDelay = 20;

    public DMData() {
        super(DATA_NAME);
        this.gameTeamKill = new HashMap<>();
        this.gamePlayerKill = new HashMap<>();
        this.trackedRestandingGamePlayers = new HashSet<>();
        this.queuedRestandingGamePlayer = new HashMap<>();
    }

    @Override
    public void clear() {
        if (locked) return;

        gameTeamKill.clear();
        gamePlayerKill.clear();
        this.teamDirty = true;
        this.playerDirty = true;
        currentMaxKill = -1;

        trackedRestandingGamePlayers.clear();
        queuedRestandingGamePlayer.clear();
    }

    public void adjustTrackDelay(int delay) {
        if (locked) return;

        if (delay < 20) {
            BattleRoyale.LOGGER.warn("DMData: track delay {} < 20", delay);
        }
        this.trackDelay = delay;
    }

    @Override
    public void startGame() {
        if (locked) return;

        clear(); // 内部保证，无需关心外部是否手动清理

        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        teamManager.getGamePlayers().forEach(player -> gamePlayerKill.put(player, 0));
        teamManager.getGameTeams().forEach(team -> gameTeamKill.put(team, 0));
        this.teamDirty = true;
        this.playerDirty = true;
        currentMaxKill = 0;
        lockData();
    }

    @Override
    public void endGame() {
        if (locked) {
            unlockData();
        }

        // 游戏结束后缓存的 GamePlayer 可能就无效了
        clear();
    }

    public void updateTrackQueueDelay() {
        if (queuedRestandingGamePlayer.isEmpty()) return;

        Iterator<Map.Entry<GamePlayer, Integer>> iterator = queuedRestandingGamePlayer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<GamePlayer, Integer> entry = iterator.next();
            int remainingDelay = entry.getValue() - 1;

            if (remainingDelay <= 0) {
                // 延迟到期，从队列移除并加入待复活就绪集合
                trackedRestandingGamePlayers.add(entry.getKey());
                iterator.remove();
                BattleRoyale.LOGGER.debug("GamePlayer {} moved from queue to trackedRestandingGamePlayers", entry.getKey().getNameWithId());
            } else {
                // 更新剩余延迟
                entry.setValue(remainingDelay);
            }
        }
    }

    @Override
    public boolean addGamePlayerKill(GamePlayer gamePlayer, int kill) {
        if (!locked) return false;

        Integer preKills = gamePlayerKill.get(gamePlayer);
        if (preKills == null) return false;

        if (kill < 0) {
            return false;
        } else if (kill == 0) {
            return true;
        }

        int currentKill = preKills + kill;
        gamePlayerKill.put(gamePlayer, currentKill);
        currentMaxKill = Math.max(currentMaxKill, currentKill);

        this.playerDirty = true;
        return true;
    }

    @Override
    public boolean addGameTeamKill(GameTeam gameTeam, int kill) {
        if (!locked) return false;

        Integer preKills = gameTeamKill.get(gameTeam);
        if (preKills == null) return false;

        if (kill < 0) {
            return false;
        } else if (kill == 0) {
            return true;
        }

        int currentKill = preKills + kill;
        gameTeamKill.put(gameTeam, currentKill);
        currentMaxKill = Math.max(currentMaxKill, currentKill);

        this.teamDirty = true;
        return true;
    }

    public int getCurrentMaxKill() {
        return currentMaxKill;
    }

    public Set<GameTeam> getTrackedGameTeams() {
        return gameTeamKill.keySet();
    }
    public Set<GamePlayer> getTrackedGamePlayers() {
        return gamePlayerKill.keySet();
    }

    public Map<GameTeam, Integer> copyGameTeamKills() {
        return new HashMap<>(gameTeamKill);
    }
    public Map<GamePlayer, Integer> copyGamePlayerKills() {
        return new HashMap<>(gamePlayerKill);
    }

    /**
     * 获取按击杀数降序排列的队伍数据
     * 返回的 Map 键为击杀数，值为拥有该击杀数的队伍集合
     */
    public NavigableMap<Integer, Set<GameTeam>> getTeamKillsInvertedSorted() {
        if (teamDirty || cacheTeamInverted == null) {
            this.cacheTeamInverted = gameTeamKill.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            Map.Entry::getValue,
                            () -> new TreeMap<Integer, Set<GameTeam>>(Comparator.reverseOrder()), // 必须显式声明泛型，否则 Java 17 编译器无法正确推断 groupingBy 的返回类型
                            Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
                    ));
            this.teamDirty = false;
        }
        return copyInvertedMap(cacheTeamInverted);
    }
    /**
     * 获取按击杀数降序排列的玩家数据
     * 返回的 Map 键为击杀数，值为拥有该击杀数的玩家集合
     */
    public NavigableMap<Integer, Set<GamePlayer>> getPlayerKillsInvertedSorted() {
        if (playerDirty || cachePlayerInverted == null) {
            this.cachePlayerInverted = gamePlayerKill.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            Map.Entry::getValue,
                            () -> new TreeMap<Integer, Set<GamePlayer>>(Comparator.reverseOrder()), // 必须显式声明泛型，否则 Java 17 编译器无法正确推断 groupingBy 的返回类型
                            Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
                    ));
            this.playerDirty = false;
        }
        return copyInvertedMap(cachePlayerInverted);
    }
    /**
     * 获取击杀数大于或等于指定值的队伍 Map 副本
     */
    public NavigableMap<Integer, Set<GameTeam>> getTeamKillsGreaterOrEqual(int minKills) {
        if (teamDirty || cacheTeamInverted == null) {
            getTeamKillsInvertedSorted(); // 触发更新
        }
        return copyInvertedMap(cacheTeamInverted.headMap(minKills, true));
    }
    /**
     * 获取击杀数大于或等于指定值的玩家 Map 副本
     */
    public NavigableMap<Integer, Set<GamePlayer>> getPlayerKillsGreaterOrEqual(int minKills) {
        if (playerDirty || cachePlayerInverted == null) {
            getPlayerKillsInvertedSorted(); // 触发更新
        }
        return copyInvertedMap(cachePlayerInverted.headMap(minKills, true));
    }
    /**
     * 辅助方法：深拷贝 Map 及其内部的 Set，确保外部修改不会影响内部缓存
     */
    private <T> NavigableMap<Integer, Set<T>> copyInvertedMap(NavigableMap<Integer, Set<T>> source) {
        NavigableMap<Integer, Set<T>> copy = new TreeMap<>(source.comparator());
        for (Map.Entry<Integer, Set<T>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
    }


    @Override
    public boolean addAndTrackRestandingGamePlayer(GamePlayer gamePlayer) {
        if (!locked) return false;

        if (!gamePlayer.isEliminated()) {
            BattleRoyale.LOGGER.debug("Reject to add non-eliminated GamePlayer {} to trackedRestandingGamePlayers", gamePlayer.getNameWithId());
            return false;
        }

        if (trackedRestandingGamePlayers.contains(gamePlayer)) {
            BattleRoyale.LOGGER.debug("GamePlayer {} already in trackedRestandingGamePlayers", gamePlayer.getNameWithId());
            return false;
        } else if (queuedRestandingGamePlayer.containsKey(gamePlayer)) {
            BattleRoyale.LOGGER.debug("GamePlayer {} already in queuedRestandingGamePlayer", gamePlayer.getNameWithId());
            return false;
        } else {
            queuedRestandingGamePlayer.put(gamePlayer, trackDelay);
            return true;
        }
    }

    public Set<GamePlayer> getTrackedRestandingGamePlayerUnsafe() {
        return trackedRestandingGamePlayers;
    }

    public boolean removeRestandingGamePlayer(GamePlayer gamePlayer) {
        if (!locked) return false;

        boolean removedFromTracked = trackedRestandingGamePlayers.remove(gamePlayer);
        boolean removedFromQueue = queuedRestandingGamePlayer.remove(gamePlayer) != null;

        if (!removedFromTracked && !removedFromQueue) {
            BattleRoyale.LOGGER.debug("GamePlayer {} not in any restanding collections, failed to remove", gamePlayer.getNameWithId());
            return false;
        }

        if (gamePlayer.isEliminated()) {
            BattleRoyale.LOGGER.warn("GamePlayer {} is still eliminated, make sure removeRestandingGamePlayer triggered at the right time or manually set to non-eliminated", gamePlayer.getNameWithId());
        }

        return true;
    }
}