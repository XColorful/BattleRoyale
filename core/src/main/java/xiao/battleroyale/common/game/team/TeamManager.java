package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game._GameStatsManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;

public class TeamManager extends AbstractGameManager implements ITeamManager {

    private static class TeamManagerHolder {
        private static final TeamManager INSTANCE = new TeamManager();
    }

    public static TeamManager get() {
        return TeamManagerHolder.INSTANCE;
    }

    protected TeamManager() {}

    public static void init(McSide mcSide) {
    }

    @Override public String getManagerName() {
        return String.format("%s:TeamManager", BattleRoyale.MOD_ID);
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
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return;

        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> gameruleConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        int configId = gameManager.getGameruleConfigId();
        if (gameruleConfigManager == null || !(gameruleConfigManager.getConfigEntry(configId) instanceof GameruleConfig gameruleConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (brEntry == null || gameEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get BattleroyaleEntry or GameEntry from GameruleConfig by id: {}", configId);
            return;
        }

        this.teamConfig.updateWithConfig(brEntry, gameEntry);
        if (this.teamConfig.playerLimit < 1 || this.teamConfig.teamSize < 1) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.invalid_gamerule_config");
            BattleRoyale.LOGGER.warn("Invalid BattleroyaleEntry for TeamManager in initGameConfig");
            return;
        }

        _TeamManagement.removeOfflineGamePlayer(this, BattleRoyale.getGameManager().getServerLevel());
        clearOrUpdateTeamIfLimitChanged();
        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("TeamManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame() || !this.configPrepared) {
            return;
        }

        // clearOrUpdateTeamIfLimitChanged(); // initGameConfig到这里已经处理过了
        if (!this.teamConfig.autoJoinGame) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.require_manually_join", TeamCommand.joinCommand()));
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
        if (gameManager.getGameEntry().buildVanillaTeam) {
            buildVanillaTeam(serverLevel, this.teamConfig.vanillaTeamFormat, gameManager.getGameEntry().hideVanillaTeamName, false);
        }

        _GameStatsManager.recordGamerule(teamConfig);
        if (!hasEnoughPlayerTeamToStart()) { // 初始化游戏时检查并提示
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.not_enough_team_to_start").withStyle(ChatFormatting.YELLOW));
        }
        this.configPrepared = false;
        BattleRoyale.LOGGER.info("TeamManager complete initGame, total players: {}, total teams: {}", teamData.getGamePlayersTotal(), teamData.getGameTeamsList().size());
    }

    @Override
    public boolean isReady() {
        // return this.ready // 不用ready标记，因为Team会变动
        return hasEnoughPlayerTeamToStart(); // 用实时的检查判断是否准备好
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame() || !isReady()) {
            return false;
        }

        _TeamManagement.removeNoTeamGamePlayer(this); // 确保玩家均有队伍
        _TeamManagement.removeNoGamePlayerTeam(this); // 确保队伍均有玩家
        if (!hasEnoughPlayerTeamToStart()) { // init之后可能都退出了队伍，开始游戏前再次检查
            return false;
        }

        // TODO 处理人机填充

        if (gameManager.getGameEntry().buildVanillaTeam) {
            buildVanillaTeam(serverLevel, this.teamConfig.vanillaTeamFormat, gameManager.getGameEntry().hideVanillaTeamName, false);
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
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) {
            return;
        }

        // 如果在 onPlayerDeath 里被 IGameProcessManager 嵌套触发 (已延迟) 则不提前让 GameManager 进行结束检查
        // 避免直接干预 GameProcess
        if (gameManager.addFinishCheckAfterDeathEvent()) {
            return;
        }
        // 相当于 IGameProcessManager 的延申
        gameManager.checkIfGameShouldEnd();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.teamData.endGame(); // 解锁，清除standingGamePlayer使GameMessage重置
        GameMessageManager.notifyAliveChange();
        this.configPrepared = false;
        // this.ready = false; // 不使用ready标记，因为Team会变动

        IGameManager gameManager = BattleRoyale.getGameManager();
        GameEntry gameEntry = gameManager.getGameEntry();
        if (gameEntry != null // 1.stopGame现在在每次服务器关闭都会触发，在未读取配置时关闭会触发; 2.配置被其他模组unregistered了，为空
                && !gameEntry.keepTeamAfterGame) {
            // 退出原版队伍
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
        if (BattleRoyale.getGameManager().isInGame()) {
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
    @Override public boolean onlyRemainBotTeam() { return teamData.isOnlyRemainBotTeam(); }
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
    @Override public List<GameTeam> getGameTeams() {
        return teamData.getGameTeamsList();
    }
    @Override public List<GamePlayer> getStandingGamePlayers() {
        return teamData.getStandingGamePlayersList();
    }
    @Override public int getStandingGamePlayerSize() {
        return teamData.getStandingGamePlayerSize();
    }
    @Override public List<GameTeam> getStandingGameTeams() {
        return teamData.getStandingGameTeamsList();
    }
    @Override public @Nullable GamePlayer getRandomStandingGamePlayer() {
        List<GamePlayer> standingGamePlayers = getStandingGamePlayers();
        if (standingGamePlayers.isEmpty()) {
            return null;
        }
        return standingGamePlayers.get(BattleRoyale.COMMON_RANDOM.nextInt(standingGamePlayers.size()));
    }
    @Override public int getGamePlayersTotal() {
        return teamData.getGamePlayersTotal();
    }

    // -------TeamNofitication-------

    public void sendPlayerTeamId(ServerPlayer player) {
        _TeamNotification.sendPlayerTeamId(getGamePlayerByUUID(player.getUUID()), player);
    }

    // -------TeamExternal-------

    public void joinTeam(ServerPlayer player) {
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.joinTeam(this, player);
    }
    public void joinTeamSpecific(ServerPlayer player, int teamId) {
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.joinTeamSpecific(this, player, teamId);
    }
    public void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.kickPlayer(this, sender, targetPlayer);
    }
    public void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.invitePlayer(this, sender, targetPlayer);
    }
    public void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.acceptInvite(this, player, senderPlayer);
    }
    public void declineInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.declineInvite(this, player, senderPlayer);
    }
    public void requestPlayer(ServerPlayer sender, ServerPlayer targetPlayer) { // 申请者，目标玩家
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.requestPlayer(this, sender, targetPlayer);
    }
    public void acceptRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.acceptRequest(this, teamLeader, requesterPlayer);
    }
    public void declineRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (BattleRoyale.getGameManager().isInGame()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        _TeamExternal.declineRequest(this, teamLeader, requesterPlayer);
    }
    public boolean leaveTeam(@NotNull ServerPlayer player) {
        return _TeamExternal.leaveTeam(this, player);
    }
    public boolean addToTeam(@Nullable CommandSourceStack source, LivingEntity player, int teamId) {
        return _TeamExternal.addToTeam(this, source, player, teamId);
    }
    public int buildTeamForAll(@Nullable CommandSourceStack source, List<LivingEntity> players, int targetSize, boolean forceRebuild) {
        return _TeamExternal.buildTeamForAll(this, source, players, targetSize, forceRebuild);
    }

    // -------TeamManagement-------

    /**
     * 玩家强制加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    public void forceJoinTeam(LivingEntity player) {
        if (BattleRoyale.getGameManager().isInGame()) {
            if (player instanceof ServerPlayer serverPlayer) {
                ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            }
            return;
        }

        _TeamManagement.forceJoinTeam(this, player);
    }
    public void forceJoinTeam(LivingEntity player, int teamId) {
        if (BattleRoyale.getGameManager().isInGame()) {
            if (player instanceof ServerPlayer serverPlayer) {
                ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            }
            return;
        }

        _TeamManagement.forceJoinTeam(this, player, teamId);
    }

    public boolean forceEliminatePlayerSilence(GamePlayer gamePlayer) {
        if (!BattleRoyale.getGameManager().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager isn't in game, skipped forceEliminatePlayerSilence");
            return false;
        }

        return _TeamManagement.forceEliminatePlayerSilence(this, gamePlayer);
    }
    public void forceEliminatePlayerFromTeam(LivingEntity livingEntity) {
        if (!BattleRoyale.getGameManager().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager isn't in game, skipped forceEliminatePlayerFromTeam");
            return;
        }

        _TeamManagement.forceEliminatePlayerFromTeam(this, livingEntity);
    }
    public boolean removePlayerFromTeam(@NotNull UUID playerUUID) {
        if (BattleRoyale.getGameManager().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, skipped removePlayerFromTeam");
            return false;
        }

        return _TeamManagement.removePlayerFromTeam(this, playerUUID);
    }

    // -------TeamUtils-------

    public int getNonBotTeamCount() {
        return _TeamUtils.getNonBotTeamCount(this);
    }
    public int getStandingPlayerTeamCount() {
        return _TeamUtils.getStandingPlayerTeamCount(this);
    }
    public int getStandingTeamCount() {
        return this.teamData.getTotalStandingTeamCount();
    }
    public int findNotFullTeamId() {
        return _TeamUtils.findNotFullTeamId(this);
    }
    public boolean hasEnoughPlayerTeamToStart() {
        return _TeamUtils.hasEnoughPlayerTeamToStart(this);
    }

    public boolean buildVanillaTeam(@Nullable ServerLevel serverLevel, String vanillaTeamFormat, boolean hideName, boolean allowBuildInGame) {
        if (!allowBuildInGame && BattleRoyale.getGameManager().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, reject to build vanilla team");
            return false;
        }
        if (serverLevel == null) {
            BattleRoyale.LOGGER.error("TeamManager::buildVanillaTeamForAllGameTeams received a null ServerLevel, skipped build vanilla team");
            return false;
        }

        return _TeamUtils.buildVanillaTeamForAllGameTeams(this, serverLevel, vanillaTeamFormat, hideName);
    }
    public void clearVanillaTeam(@Nullable ServerLevel serverLevel) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("TeamManager::clearVanillaTeam received a null ServerLevel, skipped clear vanilla team");
            return;
        }

        _TeamUtils.clearVanillaTeam(this, serverLevel);
    }
    public int removeVanillaTeam(@NotNull ServerLevel serverLevel, boolean gameTeamOnly) {
        return _TeamUtils.removeVanillaTeam(this, serverLevel, gameTeamOnly);
    }
}