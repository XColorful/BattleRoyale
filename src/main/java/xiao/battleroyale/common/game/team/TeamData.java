package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;

import java.util.*;

public class TeamData {

    private final List<GamePlayer> gamePlayersList = new ArrayList<>();
    private final List<GamePlayer> standingGamePlayers = new ArrayList<>();
    private final List<GameTeam> gameTeamsList = new ArrayList<>();
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private final Map<Integer, GameTeam> gameTeams = new HashMap<>();
    private final Map<Integer, GamePlayer> gamePlayersById = new HashMap<>();

    private final Set<Integer> availablePlayerIds = new TreeSet<>();
    private final Set<Integer> availableTeamIds = new TreeSet<>();

    private boolean locked = false;
    private int maxPlayersLimit = 0;

    public void lockData() {
        this.locked = true;
        BattleRoyale.LOGGER.debug("TeamData 已锁定，禁止清空。");
    }

    public void unlockData() {
        this.locked = false;
        BattleRoyale.LOGGER.debug("TeamData 已解锁，允许清空。");
    }

    public void clear(int maxPlayers) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试清除。操作已跳过。");
            return;
        }
        unlockData();

        gamePlayersList.clear();
        standingGamePlayers.clear();
        gameTeamsList.clear();
        gamePlayers.clear();
        gamePlayersById.clear();
        gameTeams.clear();

        availablePlayerIds.clear();
        availableTeamIds.clear();

        this.maxPlayersLimit = maxPlayers;

        for (int i = 1; i <= maxPlayers; i++) {
            availablePlayerIds.add(i);
        }

        for (int i = 1; i <= maxPlayers; i++) {
            availableTeamIds.add(i);
        }

        BattleRoyale.LOGGER.info("TeamData 已清空并根据最大玩家数量 {} 预填充ID。", maxPlayers);
    }

    public int generateNextPlayerId() {
        if (availablePlayerIds.isEmpty()) {
            BattleRoyale.LOGGER.warn("Failed to generate next playerId: No available player IDs left. Max players: {}", maxPlayersLimit);
            return -1;
        }
        return availablePlayerIds.iterator().next();
    }

    public int generateNextTeamId() {
        if (availableTeamIds.isEmpty()) {
            BattleRoyale.LOGGER.warn("Failed to generate next teamId: No available team IDs left. Max players: {}", maxPlayersLimit);
            return -1;
        }
        return availableTeamIds.iterator().next();
    }

    public void startGame() {
        if (standingGamePlayers.isEmpty()) {
            standingGamePlayers.addAll(gamePlayersList);
            lockData();
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

        if (gamePlayers.containsKey(gamePlayer.getPlayerUUID())) {
            BattleRoyale.LOGGER.warn("尝试添加玩家 {} (UUID: {}) 到队伍 {}，但该玩家已存在于数据中。操作已跳过。", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID(), gameTeam.getGameTeamId());
            return;
        }

        int playerId = gamePlayer.getGameSingleId();
        if (gamePlayersById.containsKey(playerId)) {
            BattleRoyale.LOGGER.warn("尝试添加玩家 {} (ID: {}) 到队伍 {}，但该 GameSingleId 已被占用。操作已跳过。", gamePlayer.getPlayerName(), playerId, gameTeam.getGameTeamId());
            return;
        }

        if (playerId <= 0 || playerId > maxPlayersLimit || !availablePlayerIds.remove(playerId)) {
            BattleRoyale.LOGGER.warn("尝试添加玩家 {} (ID: {})，但其 ID 无效或不在可用列表中。操作已跳过。", gamePlayer.getPlayerName(), playerId);
            return;
        }

        gamePlayer.setTeam(gameTeam);
        gameTeam.addPlayer(gamePlayer);

        gamePlayers.put(gamePlayer.getPlayerUUID(), gamePlayer);
        gamePlayersById.put(playerId, gamePlayer);
        gamePlayersList.add(gamePlayer);

        if (!gameTeams.containsKey(gameTeam.getGameTeamId())) {
            addGameTeam(gameTeam);
        }
        BattleRoyale.LOGGER.debug("将玩家 {} 添加到队伍 {}", gamePlayer.getPlayerName(), gameTeam.getGameTeamId());
    }

    public void addGameTeam(@NotNull GameTeam gameTeam) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData 在锁定状态下尝试添加 GameTeam {} 。操作已跳过。", gameTeam.getGameTeamId());
            return;
        }
        if (gameTeams.containsKey(gameTeam.getGameTeamId())) {
            BattleRoyale.LOGGER.warn("尝试添加已存在的队伍 ID 的 GameTeam：{}。操作已跳过。", gameTeam.getGameTeamId());
            return;
        }

        int teamId = gameTeam.getGameTeamId();
        if (teamId <= 0 || teamId > maxPlayersLimit || !availableTeamIds.remove(teamId)) {
            BattleRoyale.LOGGER.warn("尝试添加队伍 {}，但其 ID 无效或不在可用列表中。操作已跳过。", teamId);
            return;
        }

        gameTeamsList.add(gameTeam);
        gameTeams.put(teamId, gameTeam);
        BattleRoyale.LOGGER.debug("添加了新队伍，ID 为 {}", teamId);
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
        gamePlayersById.remove(removedPlayer.getGameSingleId());
        gamePlayersList.remove(removedPlayer);

        GameTeam team = removedPlayer.getTeam();
        if (team != null) {
            team.removePlayer(removedPlayer);
            if (team.getTeamMemberCount() == 0 && gameTeams.containsKey(team.getGameTeamId())) {
                removeTeam(team.getGameTeamId());
            }
        }
        if (removedPlayer.getGameSingleId() > 0 && removedPlayer.getGameSingleId() <= maxPlayersLimit) {
            availablePlayerIds.add(removedPlayer.getGameSingleId());
        } else {
            BattleRoyale.LOGGER.warn("尝试回收无效的玩家 ID: {} (玩家: {})", removedPlayer.getGameSingleId(), removedPlayer.getPlayerName());
        }

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
                gamePlayersById.remove(player.getGameSingleId());
                gamePlayersList.remove(player);
                if (player.getGameSingleId() > 0 && player.getGameSingleId() <= maxPlayersLimit) {
                    availablePlayerIds.add(player.getGameSingleId());
                } else {
                    BattleRoyale.LOGGER.warn("尝试回收无效的玩家 ID: {} (玩家: {})", player.getGameSingleId(), player.getPlayerName());
                }
            }
            if (teamId > 0 && teamId <= maxPlayersLimit) {
                availableTeamIds.add(teamId);
            } else {
                BattleRoyale.LOGGER.warn("尝试回收无效的队伍 ID: {}", teamId);
            }
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

    public @Nullable GamePlayer getGamePlayerByGameSingleId(int playerId) {
        return gamePlayersById.get(playerId);
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

    public int getTotalPlayerCount() { return gamePlayersList.size(); }
    public int getTotalStandingPlayerCount() { return standingGamePlayers.size(); }
    public int getTotalTeamCount() { return gameTeamsList.size(); }

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
            addGameTeam(newTeam);
        }
        BattleRoyale.LOGGER.debug("玩家 {} 已从队伍 {} 切换到队伍 {}。", player.getPlayerName(), oldTeam != null ? oldTeam.getGameTeamId() : "无", newTeam.getGameTeamId());
    }
}