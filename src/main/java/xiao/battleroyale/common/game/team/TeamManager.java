package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class TeamManager extends AbstractGameManager {

    private static TeamManager instance;

    private int playerLimit;
    private int teamSize;
    private boolean aiTeammate;
    private boolean aiEnemy;
    private boolean autoJoinGame;

    private final TeamData teamData = new TeamData();

    private static final String[] TEAM_COLORS = {
            "#E9ECEC", "#F07613", "#BD44B3", "#3AAFD9", "#F8C627", "#70B919", "#ED8DAC", "#8E8E86",
            "#A0A0A0", "#158991", "#792AAC", "#35399D", "#724728", "#546D1B", "#A02722", "#141519"
    };

    private TeamManager() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new TeamManager();
        }
    }

    @NotNull
    public static TeamManager get() {
        if (instance == null) {
            TeamManager.init();
        }
        return instance;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.warn("Attempted to call initGameConfig while game is in progress. Operation skipped.");
            return;
        }

        int gameId = GameManager.get().getGameruleConfigId();
        BattleroyaleEntry brEntry = GameruleConfigManager.get().getGameruleConfig(gameId).getBattleRoyaleEntry();
        if (brEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get BattleroyaleEntry from GameruleConfig by id: {}", gameId);
            return;
        }
        this.playerLimit = brEntry.playerTotal;
        this.teamSize = brEntry.teamSize;
        this.aiTeammate = brEntry.aiTeammate;
        this.aiEnemy = brEntry.aiEnemy;
        this.autoJoinGame = brEntry.autoJoinGame;

        if (playerLimit < 1 || teamSize < 1) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Invalid BattleroyaleEntry for TeamManager in initGameConfig");
            return;
        }

        clearTeamInfo();
        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (!this.autoJoinGame) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.require_manually_join");
            BattleRoyale.LOGGER.info("AutoJoinGame is disabled, players must manually join");
        }

        List<ServerPlayer> onlinePlayers = serverLevel.getPlayers(p -> true);
        Collections.shuffle(onlinePlayers);
        if (onlinePlayers.size() > this.playerLimit) {
            onlinePlayers = onlinePlayers.subList(0, this.playerLimit);
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.reached_player_limit", this.playerLimit).withStyle(ChatFormatting.YELLOW));
        }

        clearTeamInfo();

        int playerCounter = 0;
        for (ServerPlayer player : onlinePlayers) {
            addPlayerToTeam(player);
            playerCounter++;
        }

        BattleRoyale.LOGGER.info("TeamManager complete initGame, total players: {}, total teams: {}", playerCounter, teamData.getGameTeams().size());

        if (hasEnoughPlayerToStart()) {
            this.ready = true;
        } else {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.not_enough_team_to_start").withStyle(ChatFormatting.YELLOW));
            this.ready = false;
        }
        if (this.ready) {
            teamData.lockData();
        }
    }

    public void stopGame(ServerLevel serverLevel) {
        clearTeamInfo();
        this.prepared = false;
        this.ready = false;
        teamData.unlockData();
        BattleRoyale.LOGGER.info("TeamManager stopped, clear all team info");
    }

    public void onPlayerDeath(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            BattleRoyale.LOGGER.info("onPlayerDeath: GamePlayer not found for UUID: {}, skipped", player.getUUID());
            return;
        }
        GameTeam gameTeam = gamePlayer.getTeam();
        boolean teamAliveBefore = gameTeam.isTeamAlive();
        boolean teamAliveAfter = true;
        gamePlayer.setAlive(false);
        if (!gameTeam.isTeamAlive()) {
            gamePlayer.setEliminated(true);
            teamAliveAfter = false;
        }
        if (teamAliveBefore && !teamAliveAfter) {
            BattleRoyale.LOGGER.info("Team {} has been eliminated", gameTeam.getGameTeamId());
        }

        onTeamChangedInGame();
    }

    public void onTeamChangedInGame() {
        if (!GameManager.get().isInGame()) {
            return;
        }
        // TODO 检查游戏是否提前结束(只有一队玩家)，通知GameManager
    }

    public boolean hasEnoughStandingTeam() {
        int standingTeamCount = 0;
        for (GameTeam gameteam : teamData.getGameTeams()) {
            if (gameteam.isTeamAlive()) {
                standingTeamCount++;
            }
        }
        return standingTeamCount > 1;
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(true);
            BattleRoyale.LOGGER.info("GamePlayer loggin in, set to active entity");
            return;
        }

        if (!autoJoinGame || GameManager.get().isInGame()) {
            return;
        }
        addPlayerToTeam(player);
    }

    public void onPlayerQuitGame(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            BattleRoyale.LOGGER.info("GamePlayer logged out, set to inactive entity");
        }
    }

    public void addPlayerToTeam(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (teamData.getGamePlayerByUUID(playerId) != null) {
            BattleRoyale.LOGGER.info("Player (UUID: {}) is already in GamePlayer, skipped adding to team", playerId);
            return;
        }

        if (teamData.getTotalPlayerCount() >= playerLimit) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to add player {}: player limit reached ({})", player.getName().getString(), playerLimit);
            return;
        }

        GameTeam targetTeam = findOrCreateTeamToJoin();
        if (targetTeam == null) {
            BattleRoyale.LOGGER.error("Failed to find or create a team for player {}", player.getName().getString());
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), teamData.generateNextPlayerId(), targetTeam.getGameTeamColor(), false, targetTeam);
        teamData.addPlayerToTeam(gamePlayer, targetTeam);
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
        BattleRoyale.LOGGER.info("Player {} (UUID: {}) joined team {}", player.getName().getString(), playerId, targetTeam.getGameTeamId());
    }

    public void addPlayerToTeam(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Player {} attempted to join team {} while game is in progress.", player.getName().getString(), teamId);
            return;
        }

        GameTeam targetTeam = teamData.getGameTeamById(teamId);
        if (targetTeam == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to add player {} to non-existent team {}", player.getName().getString(), teamId);
            return;
        }
        if (targetTeam.getTeamMembers().size() >= this.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to add player {} to team {}: team is full", player.getName().getString(), teamId);
            return;
        }

        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            removePlayerFromTeam(player.getUUID());
        }

        gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), teamData.generateNextPlayerId(), targetTeam.getGameTeamColor(), false, targetTeam);
        teamData.addPlayerToTeam(gamePlayer, targetTeam);
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
        BattleRoyale.LOGGER.info("Player {} (UUID: {}) joined team {}", player.getName().getString(), player.getUUID(), targetTeam.getGameTeamId());
    }

    /**
     * 从队伍中移除玩家
     * @param playerId 玩家 UUID
     * @return 移除的 GamePlayer 对象，如果不存在则为 null
     */
    public @Nullable GamePlayer removePlayerFromTeam(@NotNull UUID playerId) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.warn("Attempted to remove player {} from team while game is in progress. Operation skipped.", playerId);
            return null;
        }
        GamePlayer removedPlayer = teamData.removePlayer(playerId);
        if (removedPlayer != null) {
            // 尝试获取在线玩家对象，如果玩家不在线，则不会发送消息
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                ServerPlayer onlinePlayer = serverLevel.getServer().getPlayerList().getPlayer(playerId);
                if (onlinePlayer != null) {
                    ChatUtils.sendTranslatableMessageToPlayer(onlinePlayer, Component.translatable("battleroyale.message.removed_from_team").withStyle(ChatFormatting.YELLOW));
                }
            }
            BattleRoyale.LOGGER.info("Player {} (UUID: {}) removed from team.", removedPlayer.getPlayerName(), playerId);
        }
        return removedPlayer;
    }

    /**
     * 删除一个队伍
     * @param serverLevel 所在的服务器 Level，用于发送广播消息
     * @param teamId 队伍 ID
     * @return 删除的 GameTeam 对象，如果不存在则为 null
     */
    public @Nullable GameTeam removeTeam(ServerLevel serverLevel, int teamId) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.warn("Attempted to remove team {} while game is in progress. Operation skipped.", teamId);
            return null;
        }
        GameTeam removedTeam = teamData.removeTeam(teamId);
        if (removedTeam != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_removed", teamId).withStyle(ChatFormatting.YELLOW));
            BattleRoyale.LOGGER.info("Removed team {}", teamId);
        }
        return removedTeam;
    }

    private @Nullable GameTeam findOrCreateTeamToJoin() {
        for (GameTeam team : teamData.getGameTeams()) {
            if (team.getTeamMembers().size() < this.teamSize) {
                return team;
            }
        }

        if (teamData.getTotalPlayerCount() < playerLimit) {
            int newTeamId = teamData.generateNextTeamId();
            String teamColor = TEAM_COLORS[newTeamId % TEAM_COLORS.length];
            GameTeam newTeam = new GameTeam(newTeamId, teamColor);
            teamData.addGameTeam(newTeam);
            BattleRoyale.LOGGER.info("Failed to find a suitable team, created new team with teamId: {}", newTeamId);
            return newTeam;
        }

        return null;
    }

    @Nullable
    public List<UUID> getPlayerUUIDList() {
        return teamData.getPlayerUUIDList();
    }

    @Nullable
    public List<GameTeam> getGameTeams() {
        return teamData.getGameTeams();
    }

    @Nullable
    public GamePlayer getGamePlayerByUUID(UUID playerId) {
        return teamData.getGamePlayerByUUID(playerId);
    }

    public List<GamePlayer> getGamePlayerList() {
        return teamData.getAllGamePlayers();
    }

    public int getTotalMembers() {
        return teamData.getTotalPlayerCount();
    }

    private void clearTeamInfo() {
        teamData.clear();
    }

    private boolean hasEnoughPlayerToStart() {
        return this.teamData.getGameTeams().size() > 1
                || (this.teamData.getGameTeams().size() == 1 && aiEnemy);
    }

    private static class TeamData {

        private final List<UUID> playerUUIDList = new ArrayList<>();
        private final List<GameTeam> gameTeams = new ArrayList<>();
        private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
        private final Map<Integer, GameTeam> gameTeamsById = new HashMap<>();

        private final Set<Integer> availablePlayerIds = new TreeSet<>();
        private final Set<Integer> availableTeamIds = new TreeSet<>();

        private int nextPlayerIdCounter = 1;
        private int nextTeamIdCounter = 1;

        private boolean locked = false;

        public void lockData() {
            this.locked = true;
        }

        public void unlockData() {
            this.locked = false;
        }

        private void clear() {
            if (locked) {
                BattleRoyale.LOGGER.warn("Attempted to clear TeamData while locked. Operation skipped as data integrity is prioritized during gameplay.");
                return;
            }
            playerUUIDList.clear();
            gameTeams.clear();
            gamePlayers.clear();
            gameTeamsById.clear();
            availablePlayerIds.clear();
            availableTeamIds.clear();
            nextPlayerIdCounter = 1;
            nextTeamIdCounter = 1;
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

        public void addPlayerToTeam(@NotNull GamePlayer gamePlayer, @NotNull GameTeam gameTeam) {
            gameTeam.addPlayer(gamePlayer);
            UUID playerId = gamePlayer.getPlayerUUID();
            playerUUIDList.add(playerId);
            gamePlayers.put(playerId, gamePlayer);
            int teamId = gameTeam.getGameTeamId();
            if (!gameTeamsById.containsKey(teamId)) {
                gameTeamsById.put(teamId, gameTeam);
                gameTeams.add(gameTeam);
            }
        }

        public void addGameTeam(@NotNull GameTeam gameTeam) {
            if (locked) {
                BattleRoyale.LOGGER.warn("Attempted to add GameTeam while locked. Operation skipped.");
                return;
            }
            if (gameTeamsById.containsKey(gameTeam.getGameTeamId())) {
                BattleRoyale.LOGGER.warn("Attempting to add GameTeam with existing teamId: {}", gameTeam.getGameTeamId());
                return;
            }
            gameTeams.add(gameTeam);
            gameTeamsById.put(gameTeam.getGameTeamId(), gameTeam);
        }

        public @Nullable GamePlayer removePlayer(@NotNull UUID playerId) {
            if (locked) {
                BattleRoyale.LOGGER.warn("Attempted to remove player {} while TeamData is locked. Operation skipped.", playerId);
                return null;
            }
            GamePlayer removedPlayer = gamePlayers.remove(playerId);
            if (removedPlayer != null) {
                playerUUIDList.remove(playerId);
                GameTeam team = removedPlayer.getTeam();
                if (team != null) {
                    team.removePlayer(removedPlayer);
                    if (team.getTeamMemberCount() == 0) {
                        removeTeam(team.getGameTeamId());
                    }
                }
                availablePlayerIds.add(removedPlayer.getGameSingleId());
            }
            return removedPlayer;
        }

        public @Nullable GameTeam removeTeam(int teamId) {
            if (locked) {
                BattleRoyale.LOGGER.warn("Attempted to remove team {} while TeamData is locked. Operation skipped.", teamId);
                return null;
            }
            GameTeam removedTeam = gameTeamsById.remove(teamId);
            if (removedTeam != null) {
                gameTeams.remove(removedTeam);
                for (GamePlayer player : new ArrayList<>(removedTeam.getTeamMembers())) {
                    removePlayer(player.getPlayerUUID());
                }
                removedTeam.getTeamMembers().clear();
                availableTeamIds.add(removedTeam.getGameTeamId());
            }
            return removedTeam;
        }

        @Nullable
        public List<UUID> getPlayerUUIDList() {
            return playerUUIDList;
        }

        public List<GameTeam> getGameTeams() {
            return gameTeams;
        }

        @Nullable
        public GamePlayer getGamePlayerByUUID(UUID playerId) {
            return gamePlayers.get(playerId);
        }

        @Nullable
        public GameTeam getGameTeamById(int teamId) {
            return gameTeamsById.get(teamId);
        }

        @Nullable
        public List<GamePlayer> getAllGamePlayers() {
            List<GamePlayer> allPlayers = new ArrayList<>();
            for (GameTeam team : gameTeams) {
                allPlayers.addAll(team.getTeamMembers());
            }
            return allPlayers;
        }

        public int getTotalPlayerCount() {
            return playerUUIDList.size();
        }
    }
}