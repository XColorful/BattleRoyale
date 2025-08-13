package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.List;

public class TeamManager extends AbstractGameManager {

    private static class TeamManagerHolder {
        private static final TeamManager INSTANCE = new TeamManager();
    }

    public static TeamManager get() {
        return TeamManagerHolder.INSTANCE;
    }

    private TeamManager() {}

    public static void init() {
        ;
    }

    private final TeamConfig teamConfig = new TeamConfig();
    public int getPlayerLimit() { return teamConfig.playerLimit; }
    public boolean shouldAutoJoin() { return this.teamConfig.autoJoinGame; }
    private final TeamData teamData = new TeamData();

    private record TeamInvite(UUID targetPlayerUUID, String targetPlayerName, int teamId, long expiryTime) {}
    private final Map<UUID, TeamInvite> pendingInvites = new HashMap<>(); // 键是发送者的 UUID
    private record TeamRequest(UUID targetTeamLeaderUUID, String targetTeamLeaderName, int requestedTeamId, long expireTime) {}
    private final Map<UUID, TeamRequest> pendingRequests = new HashMap<>(); // 键是申请者的 UUID

    private boolean isStoppingGame = false;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int configId = GameManager.get().getGameruleConfigId();
        GameruleConfigManager.GameruleConfig gameruleConfig = GameConfigManager.get().getGameruleConfig(configId);
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
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.reached_player_limit", this.teamConfig.playerLimit).withStyle(ChatFormatting.YELLOW));
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

        GameManager.get().recordGamerule(teamConfig);
        if (!hasEnoughPlayerTeamToStart()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.not_enough_team_to_start").withStyle(ChatFormatting.YELLOW));
        }
        this.configPrepared = false;
        BattleRoyale.LOGGER.info("TeamManager complete initGame, total players: {}, total teams: {}", teamData.getTotalPlayerCount(), teamData.getGameTeamsList().size());
    }

    @Override
    public boolean isReady() {
        // return this.ready // 不用ready标记，因为Team会变动
        return hasEnoughPlayerTeamToStart();
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        BattleRoyale.LOGGER.info("Attempt to start Game");
        if (GameManager.get().isInGame() || !isReady()) {
            return false;
        }

        removeNoTeamPlayer(); // 确保玩家均有队伍
        if (!hasEnoughPlayerTeamToStart()) { // init之后可能都退出了队伍
            return false;
        }
        // TODO 处理人机填充，创建MC原版队伍
        teamData.startGame();
        return true;
    }

    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    /**
     * 清理掉离线GamePlayer，防止后续影响游戏结束的人数判定
     */
    private void removeOfflineGamePlayer() {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        List<GamePlayer> offlineGamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamData.getGamePlayersList()) {
            if (!gamePlayer.isActiveEntity()) {
                offlineGamePlayers.add(gamePlayer);
                continue;
            }
            if (serverLevel != null) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player == null) {
                    offlineGamePlayers.add(gamePlayer);
                }
            }
        }

        for (GamePlayer gamePlayer : offlineGamePlayers) {
            String playerName = gamePlayer.getPlayerName();
            if (teamData.removePlayer(gamePlayer)) {
                BattleRoyale.LOGGER.debug("Removed offline gamePlayer {}", playerName);
            }
        }
    }

    /**
     * 防止游戏开始时有意外的无队伍GamePlayer
     */
    private void removeNoTeamPlayer() {
        List<GamePlayer> noTeamPlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamData.getGamePlayersList()) {
            if (gamePlayer.getTeam() == null) {
                noTeamPlayers.add(gamePlayer);
            }
        }

        for (GamePlayer noTeamPlayer : noTeamPlayers) {
            if (teamData.removePlayer(noTeamPlayer)) {
                GameManager.get().notifyLeavedMember(noTeamPlayer.getPlayerUUID(), noTeamPlayer.getGameTeamId()); // 防止游戏开始时无队伍的GamePlayer
            }
        }
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.teamData.endGame(); // 解锁，清除standingGamePlayer使GameMessage重置
        GameManager.get().notifyAliveChange();
        this.configPrepared = false;
        // this.ready = false; // 不使用ready标记，因为Team会变动

        GameManager gameManager = GameManager.get();
        if (!gameManager.getGameEntry().keepTeamAfterGame) {
            for (GameTeam gameTeam : getGameTeamsList()) { // 新增双重保险，照理应该要能成功发送清空队伍的消息
                gameManager.notifyTeamChange(gameTeam.getGameTeamId());
            }
            isStoppingGame = true; // 这个变量会阻止获取GameTeam
            for (GamePlayer gamePlayer : getGamePlayersList()) { // 触发频率低，问题不大。。。
                gameManager.notifyLeavedMember(gamePlayer.getPlayerUUID(), gamePlayer.getGameTeamId());
            }
            if (serverLevel != null) {
                BattleRoyale.LOGGER.debug("TeamManager start delayed clear()");
                // 延迟2tick保证MessageManager获取到GamePlayer并保证在发送的tick之后再执行this.clear()
                serverLevel.getServer().execute(() -> {
                    serverLevel.getServer().execute(() -> {
                        this.clear(); // 延迟2tick的clear
                        isStoppingGame = false;
                        BattleRoyale.LOGGER.debug("TeamManager finished delayed clear()");
                    });
                });
            } else {
                BattleRoyale.LOGGER.debug("TeamManager start instant clear()");
                this.clear(); // stopGame立即执行的clear
                isStoppingGame = false;
            }
        }
    }

    /**
     * 通常在人数变更的时候可能提前结束游戏，手动提醒以降低 GameManager 检查频率
     */
    private void onTeamChangedInGame() {
        if (!GameManager.get().isInGame()) {
            return;
        }

        GameManager.get().checkIfGameShouldEnd();
    }

    public int getStandingTeamCount() {
        return teamData.getTotalStandingTeamCount();
    }

    /**
     * 返回非人机队伍数量
     */
    public int getPlayerTeamCount() {
        int count = 0;
        Set<Integer> playerTeamId = new HashSet<>();
        for (GamePlayer gamePlayer : getGamePlayersList()) {
            if (!gamePlayer.isBot()) {
                int teamId = gamePlayer.getGameTeamId();
                if (!playerTeamId.contains(teamId)) {
                    playerTeamId.add(teamId);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 返回未被淘汰的非人机队伍数量
     */
    public int getStandingPlayerTeamCount() {
        int count = 0;
        Set<Integer> playerTeamId = new HashSet<>();
        for (GamePlayer gamePlayer : getStandingGamePlayersList()) {
            if (!gamePlayer.isBot()) {
                int teamId = gamePlayer.getGameTeamId();
                if (!playerTeamId.contains(teamId)) {
                    playerTeamId.add(teamId);
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isPlayerLeader(UUID playerUUID) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            return false;
        }
        GameTeam gameTeam = gamePlayer.getTeam();
        return gameTeam.isLeader(playerUUID);
    }

    /**
     * 玩家强制加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    public void forceJoinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        int newTeamId = findNotFullTeamId();
        if (newTeamId > 0) { // 有未满员队伍
            addPlayerToTeamInternal(player, findNotFullTeamId(), false); // 直接强制加入
        } else {
            newTeamId = teamData.generateNextTeamId();
            createNewTeamAndJoin(player, newTeamId); // 无未满员队伍则创建队伍
        }
    }

    /**
     * 玩家加入游戏，优先创建队伍，无法创建队伍则发送申请
     * @param player 需要加入队伍的玩家
     */
    public void joinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        int newTeamId = teamData.generateNextTeamId();
        if (createNewTeamAndJoin(player, newTeamId)) { // 默认尝试创建队伍
            return;
        }

        addPlayerToTeamInternal(player, findNotFullTeamId(), true); // 尝试申请加入
    }

    /**
     * 玩家尝试创建一个指定的队伍 (已存在则改为申请)。
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    public void joinTeamSpecific(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        if (createNewTeamAndJoin(player, teamId)) { // 手动加入队伍
            return;
        }

        addPlayerToTeamInternal(player, teamId, true); // 无法创建则尝试申请加入
    }

    /**
     * 指定加入的队伍，不自动将申请的玩家离开队伍
     * @param player 需要加入队伍的玩家
     * @param targetTeamId 目标队伍的 ID
     * @param request 如果为 true，则尝试直接加入（跳过队长确认）；如果为 false，则当队伍有在线成员时发送申请。
     */
    private void addPlayerToTeamInternal(ServerPlayer player, int targetTeamId, boolean request) {
        UUID playerId = player.getUUID();
        GameTeam targetTeam = teamData.getGameTeamById(targetTeamId);
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (targetTeam == null || serverLevel == null) { // 队伍不存在直接跳过
            return;
        } else if (teamData.getGamePlayerByUUID(playerId) != null) { // 不自动离开队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.YELLOW));
            return;
        } else if (targetTeam.getTeamMembers().size() >= this.teamConfig.teamSize) { // 队伍满员
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", targetTeamId).withStyle(ChatFormatting.RED));
            return;
        }

        if (!request || targetTeam.getTeamMemberCount() == 0) { // 空队伍不用申请
            // 新建 GamePlayer
            int newPlayerId = teamData.generateNextPlayerId();
            if (newPlayerId < 1) { // 达到人数上限
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit", this.teamConfig.playerLimit).withStyle(ChatFormatting.RED));
                return;
            }
            GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), newPlayerId, false, targetTeam);
            if (teamData.addPlayerToTeam(gamePlayer, targetTeam)) {
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
                notifyPlayerJoinTeam(gamePlayer); // 通知队伍成员有新玩家加入
                GameManager.get().notifyTeamChange(targetTeam.getGameTeamId()); // 玩家加入队伍，通知更新队伍HUD
                return;
            }
            BattleRoyale.LOGGER.debug("Failed to add player {} to team {}", player.getName().getString(), targetTeamId);
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
        } else { // 改为发送邀请
            RequestPlayer(player, (ServerPlayer) serverLevel.getPlayerByUUID(targetTeam.getLeaderUUID()));
        }
    }

    /**
     * 通知队伍原玩家新成员入队
     * @param newPlayer 新入队的成员
     */
    public void notifyPlayerJoinTeam(GamePlayer newPlayer) {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        GameTeam gameTeam = newPlayer.getTeam();
        if (serverLevel == null) {
            return;
        }
        String newPlayerName = newPlayer.getPlayerName();
        for (GamePlayer member : gameTeam.getTeamMembers()) {
            if (member == newPlayer) {
                continue;
            }
            ServerPlayer teamPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(member.getPlayerUUID());
            if (teamPlayer != null) {
                ChatUtils.sendTranslatableMessageToPlayer(teamPlayer, Component.translatable("battleroyale.message.player_joined_team", newPlayerName).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    public void leaveTeam(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        }

        forceEliminatePlayerFromTeam(player); // 游戏进行时生效，退出即被淘汰，不在游戏运行时则自动跳过

        if (removePlayerFromTeam(playerUUID)) { // 不在游戏时生效，手动离开当前队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.GREEN));
        }
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

        if (teamData.eliminatePlayer(gamePlayer)) {
            // 强制淘汰后传送回大厅
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null) {
                    // TODO 生成战利品盒子
                    GameManager.get().sendLobbyTeleportMessage(player, false);
                }
            }
            onTeamChangedInGame();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 在游戏中强制淘汰玩家并向队友发送消息
     */
    public void forceEliminatePlayerFromTeam(ServerPlayer player) {
        if (!GameManager.get().isInGame()) {
            return;
        }

        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        }

        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        if (teamData.eliminatePlayer(player.getUUID())) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.info("Force eliminated player {} (UUID: {})", player.getName().getString(), player.getUUID());
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.forced_elimination", player.getName()).withStyle(ChatFormatting.RED));
        }
        BattleRoyale.LOGGER.info("Force removed player {} (UUID: {})", player.getName().getString(), player.getUUID());

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            if (serverLevel != null) {
                if (!teamEliminatedBefore) {
                    ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                } else {
                    BattleRoyale.LOGGER.debug("Team has already been eliminated, TeamManager skipped sending chat message");
                }
            }
            BattleRoyale.LOGGER.info("Team {} has been eliminated for no standing player", gameTeam.getGameTeamId());
        }
        onTeamChangedInGame();
    }

    public void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (sender == null) {
            return;
        } else if (targetPlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(sender.getUUID());
        GamePlayer targetGamePlayer = teamData.getGamePlayerByUUID(targetPlayer.getUUID());

        if (senderGamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!senderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer == null || targetGamePlayer.getGameTeamId() != senderGamePlayer.getGameTeamId()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        if (removePlayerFromTeam(targetPlayer.getUUID())) { // 手动踢人
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_kicked_from_team", targetPlayer.getName()).withStyle(ChatFormatting.YELLOW));
            ChatUtils.sendTranslatableMessageToPlayer(targetPlayer, Component.translatable("battleroyale.message.kicked_by_leader", sender.getName()).withStyle(ChatFormatting.RED));
        }
    }

    public void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(sender.getUUID());
        if (senderGamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!senderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        } else if (senderGamePlayer.getTeam().getTeamMemberCount() >= this.teamConfig.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full").withStyle(ChatFormatting.RED));
            return;
        } else if (teamData.getGamePlayerByUUID(targetPlayer.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_already_in_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        int teamId = senderGamePlayer.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + teamConfig.teamMsgExpireTimeMillis;
        String targetName = targetPlayer.getName().getString();
        pendingInvites.put(sender.getUUID(), new TeamInvite(targetPlayer.getUUID(), targetName, teamId, expiryTime));
        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.invite_sent", targetName).withStyle(ChatFormatting.GREEN));

        String senderName = sender.getName().getString();
        MutableComponent message = Component.translatable("battleroyale.message.invite_received", senderName, teamId);
        String acceptCommand = TeamCommand.acceptInviteCommand(senderName);
        String declineCommand = TeamCommand.declineInviteCommand(senderName);
        MutableComponent acceptButton = Component.translatable("battleroyale.message.accept")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, acceptCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(acceptCommand)))
                );
        MutableComponent declineButton = Component.translatable("battleroyale.message.decline")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, declineCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(declineCommand)))
                );

        message.append(" ").append(acceptButton).append(" ").append(declineButton);
        ChatUtils.sendClickableMessageToPlayer(targetPlayer, message);
    }

    public void acceptInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null || player == null) {
            return;
        } else if (senderPlayer == null || !isPlayerLeader(senderPlayer.getUUID())) { // 玩家未加载或不是队长
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        }
        UUID senderUUID = senderPlayer.getUUID();
        UUID playerUUID = player.getUUID();
        TeamInvite invite = pendingInvites.get(senderUUID);
        if (invite == null || !invite.targetPlayerUUID().equals(playerUUID)) { // 已经改为向其他人发的邀请
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        } else if (invite.expiryTime() < System.currentTimeMillis()) { // 邀请是否过期
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            pendingInvites.remove(senderUUID);
            return;
        } else if (teamData.getGamePlayerByUUID(playerUUID) != null) { // 检查接收者是否已在队伍中
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.RED));
            pendingInvites.remove(senderUUID);
            return;
        }

        GameTeam targetTeam = teamData.getGameTeamById(invite.teamId());
        String playerName = player.getName().getString();
        if (targetTeam == null) { // 目标队伍不存在
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_does_not_exist", invite.teamId()).withStyle(ChatFormatting.RED));
            pendingInvites.remove(senderUUID);
            return;
        } else if (targetTeam.getTeamMembers().size() >= this.teamConfig.teamSize) { // 目标队伍满员
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", invite.teamId()).withStyle(ChatFormatting.RED));
            pendingInvites.remove(senderUUID);
            return;
        }

        pendingInvites.remove(senderUUID);
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.invite_accepted", invite.teamId()).withStyle(ChatFormatting.GREEN));
        ChatUtils.sendTranslatableMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_accept_request", playerName).withStyle(ChatFormatting.GREEN));
        addPlayerToTeamInternal(player, invite.teamId(), false); // 同意邀请，强制加入
    }

    public void declineInvite(ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null || player == null) {
            return;
        } else if (senderPlayer == null || !isPlayerLeader(senderPlayer.getUUID())) { // 玩家未加载或不是队长
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        }
        UUID senderUUID = senderPlayer.getUUID();
        UUID playerUUID = player.getUUID();
        TeamInvite invite = pendingInvites.get(senderUUID);
        if (invite == null || !invite.targetPlayerUUID().equals(playerUUID)) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        } else if (invite.expiryTime() < System.currentTimeMillis()) { // 邀请是否过期
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            pendingInvites.remove(senderUUID);
            return;
        }

        pendingInvites.remove(senderUUID);
        String playerName = player.getName().getString();
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.invite_declined", invite.teamId()).withStyle(ChatFormatting.YELLOW));
        ChatUtils.sendTranslatableMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_declined_invite", playerName).withStyle(ChatFormatting.RED));
    }

    public void RequestPlayer(ServerPlayer sender, ServerPlayer targetPlayer) { // 申请者，目标玩家
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null || sender == null) {
            return;
        } else if (targetPlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(sender.getUUID());
        GamePlayer targetGamePlayer = teamData.getGamePlayerByUUID(targetPlayer.getUUID());
        if (senderGamePlayer != null) { // 申请者已在队伍里
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer == null) { // 对方不在队伍里
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.target_not_in_a_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer.getTeam().getTeamMemberCount() >= this.teamConfig.teamSize) { // 对方队伍已满员
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        } else if (!targetGamePlayer.isLeader()) { // 对方不是队长
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_actual_leader", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        GameTeam gameTeam = targetGamePlayer.getTeam();
        int targetTeamId = gameTeam.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + teamConfig.teamMsgExpireTimeMillis;

        pendingRequests.put(sender.getUUID(), new TeamRequest(targetGamePlayer.getPlayerUUID(), targetGamePlayer.getPlayerName(), targetTeamId, expiryTime));
        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.request_sent", targetTeamId).withStyle(ChatFormatting.GREEN));

        String senderName = sender.getName().getString();
        MutableComponent message = Component.translatable("battleroyale.message.request_received", senderName);
        String acceptCommand = TeamCommand.acceptRequestCommand(senderName);
        String declineCommand = TeamCommand.declineRequestCommand(senderName);
        MutableComponent acceptButton = Component.translatable("battleroyale.message.accept")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, acceptCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(acceptCommand)))
                );
        MutableComponent declineButton = Component.translatable("battleroyale.message.decline")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, declineCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(declineCommand)))
                );
        message.append(" ").append(acceptButton).append(" ").append(declineButton);

        ChatUtils.sendClickableMessageToPlayer(targetPlayer, message);
    }

    public void acceptRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null || teamLeader == null) {
            return;
        } else if (requesterPlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer leaderGamePlayer = teamData.getGamePlayerByUUID(teamLeader.getUUID());

        if (leaderGamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!leaderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }
        UUID requesterUUID = requesterPlayer.getUUID();
        String requesterName = requesterPlayer.getName().getString();
        TeamRequest request = pendingRequests.get(requesterUUID);

        if (request == null || request.requestedTeamId() != leaderGamePlayer.getGameTeamId() // 是否是发送给队长的
                || !request.targetTeamLeaderUUID().equals(teamLeader.getUUID())) { // 已经改为向其他人发的邀请
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.no_valid_request").withStyle(ChatFormatting.RED));
            return;
        } else if (request.expireTime() < System.currentTimeMillis()) { // 检查请求是否过期
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.expired_request").withStyle(ChatFormatting.RED));
            pendingRequests.remove(requesterUUID);
            return;
        } else if (teamData.getGamePlayerByUUID(requesterUUID) != null) { // 检查申请者是否已在队伍中
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_already_in_team", requesterName).withStyle(ChatFormatting.RED));
            pendingRequests.remove(requesterUUID);
            return;
        }
        GameTeam targetTeam = leaderGamePlayer.getTeam();
        if (targetTeam.getTeamMembers().size() >= this.teamConfig.teamSize) { // 检查目标队伍是否满员
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.team_full", targetTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            pendingRequests.remove(requesterUUID);
            return;
        }

        pendingRequests.remove(requesterUUID);
        ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.request_accepted", requesterName).withStyle(ChatFormatting.GREEN));
        ChatUtils.sendTranslatableMessageToPlayer(requesterPlayer, Component.translatable("battleroyale.message.player_accept_request", teamLeader.getName().getString()).withStyle(ChatFormatting.GREEN));
        addPlayerToTeamInternal(requesterPlayer, targetTeam.getGameTeamId(), false); // 同意申请，强制加入
    }

    public void declineRequest(ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null || teamLeader == null) {
            return;
        } else if (requesterPlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer leaderGamePlayer = teamData.getGamePlayerByUUID(teamLeader.getUUID());

        if (leaderGamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!leaderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }

        UUID requesterUUID = requesterPlayer.getUUID();
        String requesterName = requesterPlayer.getName().getString();
        TeamRequest request = pendingRequests.get(requesterUUID);

        if (request == null || !request.targetTeamLeaderUUID().equals(teamLeader.getUUID())
                || request.requestedTeamId() != leaderGamePlayer.getGameTeamId()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.no_valid_request").withStyle(ChatFormatting.RED));
            return;
        } else if (request.expireTime() < System.currentTimeMillis()) {
            ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.expired_request").withStyle(ChatFormatting.RED));
            pendingRequests.remove(requesterUUID);
            return;
        }

        pendingRequests.remove(requesterUUID);
        ChatUtils.sendTranslatableMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.request_declined", requesterName).withStyle(ChatFormatting.YELLOW));
        ChatUtils.sendTranslatableMessageToPlayer(requesterPlayer, Component.translatable("battleroyale.message.player_declined_request", teamLeader.getName().getString()).withStyle(ChatFormatting.RED));
    }

    /**
     * 游戏未开始时将玩家移出队伍
     * @return 是否移出队伍
     */
    public boolean removePlayerFromTeam(@NotNull UUID playerId) {
        if (GameManager.get().isInGame()) {
            return false;
        }

        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(playerId);
        if (gamePlayer == null) {
            return false;
        }
        int teamId = gamePlayer.getGameTeamId();

        if (teamData.removePlayer(playerId)) {
            GameManager.get().notifyLeavedMember(playerId, teamId); // 离队后通知不渲染队伍HUD
            GameManager.get().notifyTeamChange(teamId); // 离队后通知队伍成员更新队伍HUD
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建并加入队伍
     * @param player 需要加入队伍的 ServerPlayer
     * @param teamId 队伍id
     * @return 是否加入队伍
     */
    private boolean createNewTeamAndJoin(ServerPlayer player, int teamId) {
        if (teamId < 1) {
            return false;
        }
        int newPlayerId = teamData.generateNextPlayerId();
        if (newPlayerId < 1) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit").withStyle(ChatFormatting.RED));
            return false;
        }
        GameTeam newTeam = new GameTeam(teamId, teamConfig.getTeamColor(teamId));
        if (!teamData.addGameTeam(newTeam)) {
            BattleRoyale.LOGGER.debug("Failed to create new team {} and let {} join", teamId, player.getName().getString());
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            return false;
        }
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), newPlayerId, false, newTeam);
        if (teamData.addPlayerToTeam(gamePlayer, newTeam)) {
            GameManager.get().notifyTeamChange(newTeam.getGameTeamId()); // 新建队伍并加入，通知更新队伍HUD
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", teamId).withStyle(ChatFormatting.GREEN));
            return true;
        }
        return false;
    }


    /**
     * 找到第一个未满员队伍
     * @return 可用的队伍，如无则 null
     */
    private int findNotFullTeamId() {
        if (teamData.getTotalPlayerCount() >= this.teamConfig.playerLimit) {
            return -1;
        }

        // 寻找已有的未满员队伍
        List<Integer> idList = new ArrayList<>();
        for (GameTeam team : teamData.getGameTeamsList()) {
            if (team.getTeamMembers().size() < this.teamConfig.teamSize) {
                idList.add(team.getGameTeamId());
            }
        }
        Collections.shuffle(idList);
        if (idList.isEmpty()) {
            return -1;
        }
        return idList.get(0);
    }

    public void sendPlayerTeamId(ServerPlayer player) {
        GamePlayer gamePlayer = getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        }
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.your_team_id", gamePlayer.getGameTeamId()).withStyle(ChatFormatting.AQUA));
    }

    /**
     * 传送玩家至大厅，如果正在游戏中则淘汰
     * @param player 需传送的玩家
     */
    public void teleportToLobby(ServerPlayer player) {
        if (player == null || !player.isAlive()) {
            return;
        }

        if (teamData.hasStandingGamePlayer(player.getUUID())) { // 游戏进行中，且未被淘汰
            if (GameManager.get().teleportToLobby(player)) { // 若游戏中传送成功才淘汰
                forceEliminatePlayerFromTeam(player); // 强制淘汰
            } else {
                BattleRoyale.LOGGER.error("Teleport in game player while not has lobby");
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
            }
        } else if (GameManager.get().teleportToLobby(player)) { // 传送，且传送成功
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.teleported_to_lobby").withStyle(ChatFormatting.GREEN));
        } else {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }

    public void onBotGamePlayerChanged(GamePlayer gamePlayer, UUID newPlayerUUID) {
        if (getGamePlayerByUUID(newPlayerUUID) != null) {
            return;
        }
        teamData.changeBotGamePlayer(gamePlayer, newPlayerUUID);
    }

    public List<GameTeam> getGameTeamsList() {
        return teamData.getGameTeamsList();
    }

    @Nullable
    public GameTeam getGameTeamById(int teamId) {
        if (isStoppingGame) { // TeamMessageManager通过gameTeam来build消息，特殊处理
            GameTeam gameTeam = teamData.getGameTeamById(teamId);
            BattleRoyale.LOGGER.debug("TeamManager is stopping game, return GameTeam = null, original result:{}", gameTeam != null ? gameTeam.getGameTeamId() : "null");
            return null;
        }
        return teamData.getGameTeamById(teamId);
    }

    public @Nullable GamePlayer getGamePlayerByUUID(UUID playerUUID) { return teamData.getGamePlayerByUUID(playerUUID); }

    public @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return teamData.getGamePlayerByGameSingleId(playerId); }

    public List<GamePlayer> getGamePlayersList() {
        return teamData.getGamePlayersList();
    }

    public List<GamePlayer> getStandingGamePlayersList() {
        return teamData.getStandingGamePlayersList();
    }

    public @Nullable GamePlayer getRandomStandingGamePlayer() {
        List<GamePlayer> standingGamePlayers = getStandingGamePlayersList();
        if (standingGamePlayers.isEmpty()) {
            return null;
        }
        return standingGamePlayers.get(BattleRoyale.COMMON_RANDOM.nextInt(standingGamePlayers.size()));
    }

    public boolean hasStandingGamePlayer(UUID id) { return teamData.hasStandingGamePlayer(id); }

    public int getTotalMembers() {
        return teamData.getTotalPlayerCount();
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

    /**
     * 判断是否有足够队伍开始游戏
     */
    private boolean hasEnoughPlayerTeamToStart() {
        return hasEnoughPlayerToStart() && hasEnoughTeamToStart();
    }
    // 至少要有2人
    private boolean hasEnoughPlayerToStart() {
        int totalPlayerAndBots = getTotalMembers();
        return totalPlayerAndBots > 1
                || (totalPlayerAndBots == 1 && this.teamConfig.aiEnemy);
    }
    // 至少要有2队
    private boolean hasEnoughTeamToStart() {
        if (!GameManager.get().getGameEntry().allowRemainingBot) { // 不允许剩余人机打架 -> 开局不能直接只有剩余人机
            int totalTeamCount = teamData.getTotalTeamCount();
            return totalTeamCount > 1
                    || (totalTeamCount == 1 && this.teamConfig.aiEnemy);
        } else {
            int totalPlayerTeam = getPlayerTeamCount();
            return totalPlayerTeam > 1
                    || totalPlayerTeam == 0 && this.teamConfig.aiEnemy;
        }
    }
}