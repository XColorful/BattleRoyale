package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.List;

public class TeamManager extends AbstractGameManager implements IGameTeamReadApi {

    private static class TeamManagerHolder {
        private static final TeamManager INSTANCE = new TeamManager();
    }

    public static TeamManager get() {
        return TeamManagerHolder.INSTANCE;
    }

    private TeamManager() {}

    public static void init(McSide mcSide) {
        ;
    }

    protected final TeamConfig teamConfig = new TeamConfig();
    public boolean shouldAutoJoin() { return this.teamConfig.autoJoinGame; }
    protected final TeamData teamData = new TeamData();

    protected record TeamInvite(UUID targetPlayerUUID, String targetPlayerName, int teamId, long expiryTime) {}
    protected final Map<UUID, TeamInvite> pendingInvites = new HashMap<>(); // 键是发送者的 UUID
    protected record TeamRequest(UUID targetTeamLeaderUUID, String targetTeamLeaderName, int requestedTeamId, long expireTime) {}
    protected final Map<UUID, TeamRequest> pendingRequests = new HashMap<>(); // 键是申请者的 UUID

    private boolean isStoppingGame = false;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int configId = GameManager.get().getGameruleConfigId();
        GameruleConfig gameruleConfig = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), configId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get gameruleConfig by id: {}", configId);
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (brEntry == null || gameEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get BattleroyaleEntry or GameEntry from GameruleConfig by id: {}", configId);
            return;
        }

        this.teamConfig.playerLimit = brEntry.playerTotal;
        this.teamConfig.teamSize = brEntry.teamSize;
        this.teamConfig.aiTeammate = brEntry.aiTeammate;
        this.teamConfig.aiEnemy = brEntry.aiEnemy;
        this.teamConfig.autoJoinGame = brEntry.autoJoinGame;

        this.teamConfig.setTeamMsgExpireTimeSeconds(gameEntry.teamMsgExpireTimeSeconds);
        this.teamConfig.setTeamColors(gameEntry.teamColors);

        if (this.teamConfig.playerLimit < 1 || this.teamConfig.teamSize < 1) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.invalid_gamerule_config");
            BattleRoyale.LOGGER.warn("Invalid BattleroyaleEntry for TeamManager in initGameConfig");
            return;
        }

        removeOfflineGamePlayer();
        clearOrUpdateTeamIfLimitChanged();
        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("TeamManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame() || !this.configPrepared) {
            return;
        }

        // clearOrUpdateTeamIfLimitChanged(); // initGameConfig到这里已经处理过了
        if (!this.teamConfig.autoJoinGame) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.require_manually_join");
        } else { // 自动加入队伍
            List<ServerPlayer> onlinePlayers = serverLevel.getPlayers(p -> true);
            Collections.shuffle(onlinePlayers);
            if (onlinePlayers.size() > this.teamConfig.playerLimit) {
                ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.reached_player_limit", this.teamConfig.playerLimit).withStyle(ChatFormatting.YELLOW));
                onlinePlayers = onlinePlayers.subList(0, this.teamConfig.playerLimit);
            }
            for (ServerPlayer player : onlinePlayers) {
                GamePlayer gamePlayer = getGamePlayerByUUID(player.getUUID());
                if (gamePlayer != null) { // 如果keepTeamAfterGame为false，这里应该不通过
                    // 到这里要么GamePlayer没清理掉，要么就是已经有队伍，那就保留
                    gamePlayer.reset();
                    BattleRoyale.LOGGER.debug("ServerPlayer {} is already a GamePlayer (singleId:{}, teamId:{}), skipped forceJoinTeam", player.getName().getString(), gamePlayer.getGameSingleId(), gamePlayer.getGameTeamId());
                    continue;
                }
                forceJoinTeam(player); // 初始化时先强制分配，后续调整玩家自行处理
            }
        }
        GameManager gameManager = GameManager.get();
        if (gameManager.getGameEntry().buildVanillaTeam) {
            buildVanillaTeam(serverLevel, gameManager.getGameEntry().hideVanillaTeamName);
        }

        GameStatsManager.recordGamerule(teamConfig);
        if (!hasEnoughPlayerTeamToStart()) { // 初始化游戏时检查并提示
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.not_enough_team_to_start").withStyle(ChatFormatting.YELLOW));
        }
        this.configPrepared = false;
        BattleRoyale.LOGGER.info("TeamManager complete initGame, total players: {}, total teams: {}", teamData.getTotalPlayerCount(), teamData.getGameTeamsList().size());
    }

    @Override
    public boolean isReady() {
        // return this.ready // 不用ready标记，因为Team会变动
        return hasEnoughPlayerTeamToStart(); // 用实时的检查判断是否准备好
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        BattleRoyale.LOGGER.info("Attempt to start Game");
        GameManager gameManager = GameManager.get();
        if (gameManager.isInGame() || !isReady()) {
            return false;
        }

        removeNoTeamPlayer(); // 确保玩家均有队伍
        if (!hasEnoughPlayerTeamToStart()) { // init之后可能都退出了队伍，开始游戏前再次检查
            return false;
        }

        // TODO 处理人机填充

        if (gameManager.getGameEntry().buildVanillaTeam) {
            buildVanillaTeam(serverLevel, gameManager.getGameEntry().hideVanillaTeamName);
        }

        teamData.startGame();
        return true;
    }

    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    /**
     * 通常在人数变更的时候可能提前结束游戏，手动提醒以降低 GameManager 检查频率
     */
    protected void onTeamChangedInGame() {
        if (!GameManager.get().isInGame()) {
            return;
        }

        GameManager.get().checkIfGameShouldEnd();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.teamData.endGame(); // 解锁，清除standingGamePlayer使GameMessage重置
        GameMessageManager.notifyAliveChange();
        this.configPrepared = false;
        // this.ready = false; // 不使用ready标记，因为Team会变动

        GameManager gameManager = GameManager.get();
        GameEntry gameEntry = gameManager.getGameEntry();
        if (gameEntry != null // 1.stopGame现在在每次服务器关闭都会触发，在未读取配置时关闭会触发; 2.配置被其他模组unregistered了，为空
                && !gameEntry.keepTeamAfterGame) {
            // 移除原版队伍
            clearVanillaTeam(serverLevel);

            for (GameTeam gameTeam : getGameTeams()) { // 新增双重保险，照理应该要能成功发送清空队伍的消息
                GameMessageManager.notifyTeamChange(gameTeam.getGameTeamId());
            }
            isStoppingGame = true; // 这个变量会阻止获取GameTeam
            for (GamePlayer gamePlayer : getGamePlayers()) { // 触发频率低，问题不大。。。
                GameMessageManager.notifyLeavedMember(gamePlayer.getPlayerUUID(), gamePlayer.getGameTeamId());
            }
            isStoppingGame = false;

            // TeamMessageManager的消息中有保留旧队伍信息，不需要延迟清理
            this.clear();
            BattleRoyale.LOGGER.debug("TeamManager finished clear()");
        }
    }

    public void onBotGamePlayerChanged(GamePlayer gamePlayer, UUID newPlayerUUID) {
        if (getGamePlayerByUUID(newPlayerUUID) != null) {
            return;
        }
        teamData.changeBotGamePlayer(gamePlayer, newPlayerUUID);
    }

    /**
     * 尝试清除队伍信息，如果新的玩家数量限制缩小则清除，扩大限制则扩大可用id池
     */
    private void clearOrUpdateTeamIfLimitChanged() {
        this.teamData.adjustLimit(this.teamConfig.playerLimit, this.teamConfig.teamSize);
    }

    /**
     * 强制清除队伍信息
     */
    public void clear() {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.info("GameManager is in game, teamData skipped clear");
            return;
        }

        teamData.clear(this.teamConfig.playerLimit, this.teamConfig.teamSize);
        BattleRoyale.LOGGER.debug("TeamManager cleared teamData");
        pendingInvites.clear();
        pendingRequests.clear();
    }

    // IGameTeamReadApi
    @Override public int getPlayerLimit() { return teamConfig.playerLimit; }
    @Override public @Nullable GamePlayer getGamePlayerByUUID(UUID playerUUID) { return teamData.getGamePlayerByUUID(playerUUID); }
    @Override public @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return teamData.getGamePlayerByGameSingleId(playerId); }
    @Override public boolean hasStandingGamePlayer(UUID id) { return teamData.hasStandingGamePlayer(id); }
    @Override public List<GameTeam> getGameTeams() {
        return teamData.getGameTeamsList();
    }
    @Override public @Nullable GameTeam getGameTeamById(int teamId) {
        if (isStoppingGame) { // TeamMessageManager通过gameTeam来build消息，特殊处理
            GameTeam gameTeam = teamData.getGameTeamById(teamId);
            BattleRoyale.LOGGER.debug("TeamManager is stopping game, return GameTeam = null, original result:{}", gameTeam != null ? gameTeam.getGameTeamId() : "null");
            return null;
        }
        return teamData.getGameTeamById(teamId);
    }
    @Override public List<GamePlayer> getGamePlayers() {
        return teamData.getGamePlayersList();
    }
    @Override public List<GamePlayer> getStandingGamePlayers() {
        return teamData.getStandingGamePlayersList();
    }
    @Override public @Nullable GamePlayer getRandomStandingGamePlayer() {
        List<GamePlayer> standingGamePlayers = getStandingGamePlayers();
        if (standingGamePlayers.isEmpty()) {
            return null;
        }
        return standingGamePlayers.get(BattleRoyale.COMMON_RANDOM.nextInt(standingGamePlayers.size()));
    }
    @Override public int getTotalMembers() {
        return teamData.getTotalPlayerCount();
    }

    // -------TeamNofitication-------

    /**
     * 通知队伍原玩家新成员入队
     * @param newGamePlayer 新入队的成员
     */
    public void notifyPlayerJoinTeam(GamePlayer newGamePlayer) {
        TeamNotification.notifyPlayerJoinTeam(newGamePlayer, GameManager.get().getServerLevel());
    }
    public void sendPlayerTeamId(ServerPlayer player) {
        TeamNotification.sendPlayerTeamId(player);
    }

    // -------TeamExternal-------

    /**
     * 玩家加入游戏，优先创建队伍，无法创建队伍则发送申请
     * @param player 需要加入队伍的玩家
     */
    public void joinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.joinTeam(player);
    }
    /**
     * 玩家尝试创建一个指定的队伍 (已存在则改为申请)。
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    public void joinTeamSpecific(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.joinTeamSpecific(player, teamId);
    }
    // 踢出队伍
    public void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.kickPlayer(sender, targetPlayer);
    }
    // 邀请玩家
    public void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.invitePlayer(sender, targetPlayer);
    }
    // 接收邀请
    public void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.acceptInvite(player, senderPlayer);
    }
    // 拒绝邀请
    public void declineInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.declineInvite(player, senderPlayer);
    }
    // 申请入队
    public void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer) { // 申请者，目标玩家
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.requestPlayer(sender, targetPlayer);
    }
    // 接收申请
    public void acceptRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.acceptRequest(teamLeader, requesterPlayer);
    }
    // 拒绝申请
    public void declineRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamExternal.declineRequest(teamLeader, requesterPlayer);
    }
    // 离开队伍
    /**
     * 返回玩家是否还在队伍里
     * 在游戏中调用该函数只淘汰不离队
     */
    public boolean leaveTeam(@NotNull ServerPlayer player) {
        return TeamExternal.leaveTeam(player);
    }
    /**
     * 传送玩家至大厅，如果正在游戏中则淘汰
     * @param player 需传送的玩家
     */
    public void teleportToLobby(ServerPlayer player) {
        TeamExternal.teleportToLobby(player);
    }

    // -------TeamManagement-------

    /**
     * 玩家强制加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    public void forceJoinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamManagement.forceJoinTeam(player);
    }
    /**
     * 清理掉离线GamePlayer，防止后续影响游戏结束的人数判定
     */
    private void removeOfflineGamePlayer() {
        TeamManagement.removeOfflineGamePlayer(GameManager.get().getServerLevel());
    }
    /**
     * 防止游戏开始时有意外的无队伍GamePlayer
     */
    private void removeNoTeamPlayer() {
        TeamManagement.removeNoTeamGamePlayer();
    }
    /**
     * 在游戏中强制淘汰玩家，不包含发送系统消息
     * 成功淘汰后发送大厅传送消息
     */
    public boolean forceEliminatePlayerSilence(GamePlayer gamePlayer) {
        if (!GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager isn't in game, skipped forceEliminatePlayerSilence");
            return false;
        }

        return TeamManagement.forceEliminatePlayerSilence(gamePlayer);
    }
    /**
     * 在游戏中强制淘汰玩家并向队友发送消息
     */
    public void forceEliminatePlayerFromTeam(ServerPlayer player) {
        if (!GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager isn't in game, skipped forceEliminatePlayerFromTeam");
            return;
        }

        TeamManagement.forceEliminatePlayerFromTeam(player);
    }
    /**
     * 游戏未开始时将玩家移出队伍
     * @return 是否移出队伍
     */
    public boolean removePlayerFromTeam(@NotNull UUID playerId) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, skipped removePlayerFromTeam");
            return false;
        }

        return TeamManagement.removePlayerFromTeam(playerId);
    }

    // -------TeamUtils-------

    /**
     * 返回非人机队伍数量
     */
    public int getNonBotTeamCount() {
        return TeamUtils.getNonBotTeamCount();
    }
    /**
     * 返回未被淘汰的非人机队伍数量
     */
    public int getStandingPlayerTeamCount() {
        return TeamUtils.getStandingPlayerTeamCount();
    }
    public int getStandingTeamCount() {
        return TeamUtils.getStandingTeamCount();
    }
    /**
     * 找到第一个未满员队伍
     * @return 可用的队伍，如无则返回 -1
     */
    protected int findNotFullTeamId() {
        return TeamUtils.findNotFullTeamId();
    }
    /**
     * 判断是否有足够队伍开始游戏
     */
    protected boolean hasEnoughPlayerTeamToStart() {
        return TeamUtils.hasEnoughPlayerTeamToStart();
    }
    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 构建原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     * @param hideName 是否向其他队伍隐藏名称
     */
    public void buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName) {
        GameManager gameManager = GameManager.get();
        if (gameManager.isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, reject to build vanilla team");
            return;
        }
        if (serverLevel == null) {
            BattleRoyale.LOGGER.error("TeamManager::buildVanillaTeamForAllGameTeams received a null ServerLevel, skipped build vanilla team");
            return;
        }

        TeamUtils.buildVanillaTeamForAllGameTeams(serverLevel, hideName);
    }
    /**
     * 在传入的 ServerLevel 下为全体 GamePlayer 退出原版队伍
     * @param serverLevel 用于从 GamePlayer 获取 ServerPlayer 的维度
     */
    public void clearVanillaTeam(@Nullable ServerLevel serverLevel) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("TeamManager::clearVanillaTeam received a null ServerLevel, skipped clear vanilla team");
            return;
        }

        TeamUtils.clearVanillaTeam(serverLevel);
    }
}