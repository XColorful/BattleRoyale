package xiao.battleroyale.common.game.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManagerData;

import java.util.*;

public class TeamData extends AbstractGameManagerData {

    private static final String DATA_NAME = "TeamData";

    private final List<GamePlayer> gamePlayersList = new ArrayList<>();
    private final List<GamePlayer> standingGamePlayersList = new ArrayList<>();
    private final Map<UUID, GamePlayer> standingGamePlayers = new HashMap<>();
    private final List<GameTeam> gameTeamsList = new ArrayList<>();
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private final Map<Integer, GameTeam> gameTeams = new HashMap<>();
    private final Map<Integer, GamePlayer> gamePlayersById = new HashMap<>();

    private final Set<Integer> availablePlayerIds = new TreeSet<>();
    private final Set<Integer> availableTeamIds = new TreeSet<>();

    private int maxPlayersLimit = Integer.MAX_VALUE;
    public int getMaxPlayersLimit() { return maxPlayersLimit; }

    public TeamData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        clear(Integer.MAX_VALUE);
    }
    public void clear(int maxPlayers) {
        if (locked) {
            BattleRoyale.LOGGER.warn("TeamData is locked, skipped clear()");
            return;
        }
        unlockData();

        gamePlayersList.clear();
        standingGamePlayersList.clear();
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
            availableTeamIds.add(i);
        }
    }

    public void extendLimit(int maxPlayers) {
        if (locked) {
            return;
        }

        if (maxPlayers <= this.maxPlayersLimit) {
            return;
        }
        for (int i = this.maxPlayersLimit + 1; i <= maxPlayers; i++) {
            availablePlayerIds.add(i);
            availableTeamIds.add(i);
            this.maxPlayersLimit = maxPlayers;
        }
    }

    /**
     * 生成下一个可用的 playerId
     * 未达到人数上限则一定有返回值
     * @return 未被占用的 playerId
     */
    public int generateNextPlayerId() {
        if (availablePlayerIds.isEmpty()) {
            BattleRoyale.LOGGER.info("Failed to generate next playerId: No available player IDs left. Max players: {}", maxPlayersLimit);
            return -1;
        }
        return availablePlayerIds.iterator().next();
    }

    /**
     * 生成下一个可用的 teamId
     * 未达到队伍上限则一定有返回值
     * @return 未被占用的 teamId
     */
    public int generateNextTeamId() {
        if (availableTeamIds.isEmpty()) {
            BattleRoyale.LOGGER.info("Failed to generate next teamId: No available team IDs left. Max players: {}", maxPlayersLimit);
            return -1;
        }
        return availableTeamIds.iterator().next();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        standingGamePlayersList.addAll(gamePlayersList);
        for (GamePlayer gamePlayer : standingGamePlayersList) {
            standingGamePlayers.put(gamePlayer.getPlayerUUID(), gamePlayer);
        }
        lockData();
    }

    @Override
    public void endGame() {
        if (locked) {
            unlockData();
        }
        standingGamePlayersList.clear();
        standingGamePlayers.clear();
    }

    private boolean isTeamIdValid(int teamId) {
        return teamId > 0 && teamId <= maxPlayersLimit;
    }

    private boolean isPlayerIdValid(int playerId) {
        return playerId > 0 && playerId <= maxPlayersLimit;
    }

    public boolean addPlayerToTeam(@NotNull GamePlayer gamePlayer, @NotNull GameTeam gameTeam) {
        if (locked) {
            return false;
        }

        if (gamePlayers.containsKey(gamePlayer.getPlayerUUID())) {
            return false;
        }

        int playerId = gamePlayer.getGameSingleId();
        if (!isPlayerIdValid(playerId)) {
            return false;
        }

        if (!availablePlayerIds.remove(playerId)) {
            return false;
        }
        gamePlayer.setTeam(gameTeam);
        gameTeam.addPlayer(gamePlayer);
        gamePlayers.put(gamePlayer.getPlayerUUID(), gamePlayer);
        gamePlayersById.put(playerId, gamePlayer);
        gamePlayersList.add(gamePlayer);

        if (!gameTeams.containsKey(gameTeam.getGameTeamId())) {
            addGameTeam(gameTeam);
        }
        return true;
    }

    public boolean addGameTeam(@NotNull GameTeam gameTeam) {
        if (locked) {
            return false;
        }
        if (gameTeams.containsKey(gameTeam.getGameTeamId())) {
            return false;
        }

        int teamId = gameTeam.getGameTeamId();
        if (!isTeamIdValid(teamId)) {
            return false;
        }

        if (!availableTeamIds.remove(teamId)) {
            return false;
        }
        gameTeamsList.add(gameTeam);
        gameTeams.put(teamId, gameTeam);
        return true;
    }

    public boolean removePlayer(GamePlayer player) {
        if (locked) {
            return false;
        }

        return removePlayer(player.getPlayerUUID());
    }

    public boolean removePlayer(UUID playerId) {
        if (locked) {
            return false;
        }

        GamePlayer removedPlayer = gamePlayers.remove(playerId);
        if (removedPlayer == null) {
            return false;
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
        availablePlayerIds.add(removedPlayer.getGameSingleId());
        return true;
    }

    /**
     * 只允许在游戏中调用淘汰接口
     */
    public boolean eliminatePlayer(UUID playerId) {
        GamePlayer player = gamePlayers.get(playerId);
        return eliminatePlayer(player);
    }

    /**
     * 只允许在游戏中调用淘汰接口
     */
    public boolean eliminatePlayer(GamePlayer player) {
        if (!locked) {
            return false;
        }

        if (player != null) {
            player.setEliminated(true); // GamePlayer内部自动更新alive
            standingGamePlayersList.remove(player);
            standingGamePlayers.remove(player.getPlayerUUID());
            return true;
        }

        return false;
    }

    private void removeTeam(int teamId) {
        if (locked) {
            return;
        }

        GameTeam removedTeam = gameTeams.remove(teamId);
        if (removedTeam == null) {
            return;
        }

        gameTeamsList.remove(removedTeam);
        for (GamePlayer player : new ArrayList<>(removedTeam.getTeamMembers())) {
            gamePlayers.remove(player.getPlayerUUID());
            gamePlayersById.remove(player.getGameSingleId());
            gamePlayersList.remove(player);
            if (player.getGameSingleId() > 0 && player.getGameSingleId() <= maxPlayersLimit) {
                availablePlayerIds.add(player.getGameSingleId());
            }
        }
        availableTeamIds.add(teamId);
    }

    public @Nullable GameTeam getGameTeamById(int teamId) {
        return gameTeams.get(teamId);
    }

    public @Nullable GamePlayer getGamePlayerByUUID(UUID playerUUI) {
        return gamePlayers.get(playerUUI);
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
        return Collections.unmodifiableList(standingGamePlayersList);
    }

    public boolean hasStandingGamePlayer(UUID id) {
        return standingGamePlayers.containsKey(id);
    }

    public int getTotalPlayerCount() { return gamePlayersList.size(); }
    public int getTotalStandingPlayerCount() { return standingGamePlayersList.size(); }
    public int getTotalTeamCount() { return gameTeamsList.size(); }

    public void switchPlayerTeam(@NotNull GamePlayer player, @NotNull GameTeam newTeam) {
        if (locked) {
            return;
        }

        GameTeam oldTeam = player.getTeam();
        if (oldTeam != null && oldTeam.getGameTeamId() == newTeam.getGameTeamId()) {
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
    }
}