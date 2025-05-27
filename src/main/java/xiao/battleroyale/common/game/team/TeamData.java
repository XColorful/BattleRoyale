package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;

import java.util.*;

/**
 * 用于管理队伍和玩家的数据结构。
 */
public class TeamData {

    private final List<GamePlayer> gamePlayersList = new ArrayList<>(); // 存储所有 GamePlayer
    private final List<GamePlayer> standingGamePlayers = new ArrayList<>(); // 游戏进行时维护的存活玩家列表
    private final List<GameTeam> gameTeamsList = new ArrayList<>(); // 存储所有 GameTeam
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>(); // 通过 UUID 快速查找 GamePlayer
    private final Map<Integer, GameTeam> gameTeams = new HashMap<>(); // 通过 teamId 快速查找 GameTeam

    private final Set<Integer> availablePlayerIds = new TreeSet<>();
    private final Set<Integer> availableTeamIds = new TreeSet<>();

    private int nextPlayerIdCounter = 1;
    private int nextTeamIdCounter = 1;
    private boolean locked = false;

    public void lockData() {
        this.locked = true;
        BattleRoyale.LOGGER.debug("TeamData 已锁定，禁止清空。");
    }

    public void unlockData() {
        this.locked = false;
        BattleRoyale.LOGGER.debug("TeamData 已解锁，允许清空。");
    }

    public void clear() {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试清除。操作已跳过。");
            return;
        }
        unlockData(); // 确保解锁后才能清空
        gamePlayersList.clear();
        standingGamePlayers.clear();
        gameTeamsList.clear();
        gamePlayers.clear();
        gameTeams.clear();
        availablePlayerIds.clear();
        availableTeamIds.clear();
        nextPlayerIdCounter = 1;
        nextTeamIdCounter = 1;
        BattleRoyale.LOGGER.info("TeamData 已清空。");
    }

    public int generateNextPlayerId() {
        if (!availablePlayerIds.isEmpty()) {
            int id = availablePlayerIds.iterator().next();
            availablePlayerIds.remove(id);
            return id;
        }
        return nextPlayerIdCounter++;
    }

    public int generateNextTeamId() {
        if (!availableTeamIds.isEmpty()) {
            int id = availableTeamIds.iterator().next();
            availableTeamIds.remove(id);
            return id;
        }
        return nextTeamIdCounter++;
    }

    public void startGame() {
        if (standingGamePlayers.isEmpty()) { // 避免重复初始化
            standingGamePlayers.addAll(gamePlayersList); // 从 gamePlayersList 复制所有玩家
            lockData(); // 游戏开始时锁定数据
            BattleRoyale.LOGGER.debug("游戏开始，standingGamePlayers 已初始化，当前存活玩家数: {}", standingGamePlayers.size());
        } else {
            BattleRoyale.LOGGER.warn("startGame() 在 standingGamePlayers 已初始化时被调用，已跳过。");
        }
    }

    public void addPlayerToTeam(@NotNull GamePlayer gamePlayer, @NotNull GameTeam gameTeam) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试添加玩家 {} 到队伍 {}。操作已跳过。", gamePlayer.getPlayerName(), gameTeam.getGameTeamId());
            return;
        }

        gamePlayer.setTeam(gameTeam);
        gameTeam.addPlayer(gamePlayer);

        gamePlayers.put(gamePlayer.getPlayerUUID(), gamePlayer);
        gamePlayersList.add(gamePlayer);

        if (!standingGamePlayers.isEmpty() && !standingGamePlayers.contains(gamePlayer) && !gamePlayer.isEliminated()) {
            standingGamePlayers.add(gamePlayer);
            BattleRoyale.LOGGER.debug("游戏进行中，将新玩家 {} 添加到存活玩家列表。", gamePlayer.getPlayerName());
        }

        if (!gameTeams.containsKey(gameTeam.getGameTeamId())) {
            gameTeams.put(gameTeam.getGameTeamId(), gameTeam);
            gameTeamsList.add(gameTeam);
        }
        BattleRoyale.LOGGER.debug("将玩家 {} 添加到队伍 {}", gamePlayer.getPlayerName(), gameTeam.getGameTeamId());
    }

    public void addGameTeam(@NotNull GameTeam gameTeam) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试添加 GameTeam {} 。操作已跳过。", gameTeam.getGameTeamId());
            return;
        }
        if (gameTeams.containsKey(gameTeam.getGameTeamId())) {
            BattleRoyale.LOGGER.warn("尝试添加已存在的队伍 ID 的 GameTeam：{}", gameTeam.getGameTeamId());
            return;
        }
        gameTeamsList.add(gameTeam);
        gameTeams.put(gameTeam.getGameTeamId(), gameTeam);
        BattleRoyale.LOGGER.debug("添加了新队伍，ID 为 {}", gameTeam.getGameTeamId());
    }

    public @Nullable GamePlayer removePlayer(@NotNull UUID playerId) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试移除玩家 {}。操作已跳过。", playerId);
            return null;
        }
        GamePlayer removedPlayer = gamePlayers.remove(playerId);
        if (removedPlayer == null) {
            return null;
        }
        gamePlayersList.remove(removedPlayer);
        standingGamePlayers.remove(removedPlayer);

        GameTeam team = removedPlayer.getTeam();
        if (team != null) {
            team.removePlayer(removedPlayer);
            if (team.getTeamMemberCount() == 0 && gameTeams.containsKey(team.getGameTeamId())) {
                removeTeam(team.getGameTeamId());
            }
        }
        availablePlayerIds.add(removedPlayer.getGameSingleId());
        BattleRoyale.LOGGER.debug("从队伍数据中彻底移除了玩家 {} (ID: {})。", removedPlayer.getPlayerName(), removedPlayer.getGameSingleId());
        return removedPlayer;
    }

    public void eliminatePlayer(@NotNull UUID playerId) {
        if (!locked) {
            BattleRoyale.LOGGER.warn("拒绝在未锁定状态下淘汰玩家");
            return;
        }
        GamePlayer player = gamePlayers.get(playerId);
        if (player != null) {
            if (!player.isEliminated()) {
                player.setEliminated(true);
                standingGamePlayers.remove(player);
                BattleRoyale.LOGGER.debug("玩家 {} 已被淘汰。", player.getPlayerName());
            } else {
                BattleRoyale.LOGGER.debug("玩家 {} 已经处于淘汰状态，无需重复淘汰。", player.getPlayerName());
            }
        } else {
            BattleRoyale.LOGGER.warn("尝试淘汰一个不存在的玩家: {}", playerId);
        }
    }

    public @Nullable GameTeam removeTeam(int teamId) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试移除队伍 {}。操作已跳过。", teamId);
            return null;
        }
        GameTeam removedTeam = gameTeams.remove(teamId);
        if (removedTeam != null) {
            gameTeamsList.remove(removedTeam);
            for (GamePlayer player : new ArrayList<>(removedTeam.getTeamMembers())) {
                gamePlayers.remove(player.getPlayerUUID());
                gamePlayersList.remove(player);
                standingGamePlayers.remove(player);
                availablePlayerIds.add(player.getGameSingleId());
            }
            availableTeamIds.add(teamId);
            BattleRoyale.LOGGER.debug("从队伍数据中彻底移除了队伍 {} 及其成员。", teamId);
        }
        return removedTeam;
    }

    public @Nullable GameTeam getGameTeamById(int teamId) {
        return gameTeams.get(teamId);
    }

    public @Nullable GamePlayer getGamePlayerByUUID(UUID playerId) {
        return gamePlayers.get(playerId);
    }

    public List<GameTeam> getGameTeamsList() {
        return Collections.unmodifiableList(gameTeamsList);
    }

    public List<GamePlayer> getGamePlayersList() {
        return Collections.unmodifiableList(gamePlayersList);
    }

    public List<GamePlayer> getStandingGamePlayersList() {
        return Collections.unmodifiableList(standingGamePlayers);
    }

    public int getTotalPlayerCount() {
        return gamePlayersList.size();
    }

    public void switchPlayerTeam(@NotNull GamePlayer player, @NotNull GameTeam newTeam) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试切换玩家 {} 的队伍。操作已跳过。", player.getPlayerName());
            return;
        }
        GameTeam oldTeam = player.getTeam();
        if (oldTeam != null && oldTeam.getGameTeamId() == newTeam.getGameTeamId()) {
            BattleRoyale.LOGGER.warn("玩家 {} 尝试切换到已在的队伍 {}，操作已跳过。", player.getPlayerName(), newTeam.getGameTeamId());
            return;
        }

        if (oldTeam != null) {
            oldTeam.removePlayer(player);
            if (oldTeam.getTeamMemberCount() == 0 && gameTeams.containsKey(oldTeam.getGameTeamId())) {
                removeTeam(oldTeam.getGameTeamId());
            }
        }

        newTeam.addPlayer(player);
        if (!gameTeams.containsKey(newTeam.getGameTeamId())) {
            gameTeams.put(newTeam.getGameTeamId(), newTeam);
            gameTeamsList.add(newTeam);
        }
        BattleRoyale.LOGGER.debug("玩家 {} 已从队伍 {} 切换到队伍 {}。", player.getPlayerName(), oldTeam != null ? oldTeam.getGameTeamId() : "无", newTeam.getGameTeamId());
    }
}