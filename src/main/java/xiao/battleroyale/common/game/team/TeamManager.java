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
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.List;

public class TeamManager extends AbstractGameManager {

    private static TeamManager instance;

    private int playerLimit = 0; // 实际已经在 teamData.clear(playerLimit) 前初始化
    private int teamSize;
    private boolean aiTeammate;
    private boolean aiEnemy;
    private boolean autoJoinGame;
    public boolean shouldAutoJoin() { return this.autoJoinGame; }
    private boolean keepTeamAfterGame = true;

    private final TeamData teamData = new TeamData();

    private record TeamInvite(UUID targetPlayerUUID, String targetPlayerName, int teamId, long expiryTime) {}
    private final Map<UUID, TeamInvite> pendingInvites = new HashMap<>(); // 键是发送者的 UUID
    private record TeamRequest(UUID targetTeamLeaderUUID, String targetTeamLeaderName, int requestedTeamId, long expireTime) {}
    private final Map<UUID, TeamRequest> pendingRequests = new HashMap<>(); // 键是申请者的 UUID

    private int expireTimeSeconds = 300; // TODO 增加配置
    private int getExpireTimeMillis() { return expireTimeSeconds * 1000; }

    private static final String[] TEAM_COLORS = { // TODO 改成从配置文件读取
            "#E9ECEC", "#F07613", "#BD44B3", "#3AAFD9", "#F8C627", "#70B919", "#ED8DAC", "#8E8E86",
            "#A0A0A0", "#158991", "#792AAC", "#35399D", "#724728", "#546D1B", "#A02722", "#141519"
    };

    private TeamManager() {
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
            return;
        }

        int gameId = GameManager.get().getGameruleConfigId();
        BattleroyaleEntry brEntry = GameConfigManager.get().getGameruleConfig(gameId).getBattleRoyaleEntry();
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
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.invalid_gamerule_config");
            BattleRoyale.LOGGER.warn("Invalid BattleroyaleEntry for TeamManager in initGameConfig");
            return;
        }

        clearTeamInfo();
        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame() || !this.prepared) {
            return;
        }

        clearTeamInfo();
        if (!this.autoJoinGame) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.require_manually_join");
        } else { // 自动加入队伍
            List<ServerPlayer> onlinePlayers = serverLevel.getPlayers(p -> true);
            Collections.shuffle(onlinePlayers);
            if (onlinePlayers.size() > this.playerLimit) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.reached_player_limit", this.playerLimit).withStyle(ChatFormatting.YELLOW));
                onlinePlayers = onlinePlayers.subList(0, this.playerLimit);
            }
            for (ServerPlayer player : onlinePlayers) {
                forceJoinTeam(player); // 初始化时先强制分配，后续调整玩家自行处理
            }
        }
        BattleRoyale.LOGGER.info("TeamManager complete initGame, total players: {}, total teams: {}", teamData.getTotalPlayerCount(), teamData.getGameTeamsList().size());

        if (hasEnoughPlayerTeamToStart()) {
            this.ready = true;
            return;
        }
        ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.not_enough_team_to_start").withStyle(ChatFormatting.YELLOW));
        this.ready = false;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame() || !this.ready) {
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

    private void removeNoTeamPlayer() {
        List<GamePlayer> noTeamPlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamData.getGamePlayersList()) {
            if (gamePlayer.getTeam() == null) {
                noTeamPlayers.add(gamePlayer);
            }
        }
        if (!noTeamPlayers.isEmpty()) {
            for (GamePlayer noTeamPlayer : noTeamPlayers) {
                teamData.removePlayer(noTeamPlayer);
            }
        }
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.teamData.endGame(); // 解锁
        // clearTeamInfo(); // 不立即清除组队
        this.prepared = false;
        this.ready = false;
        BattleRoyale.LOGGER.info("TeamManager stopped, clear all team info");
    }

    /**
     * 通常在人数变更的时候可能提前结束游戏，手动提醒以降低 GameManager 检查频率
     */
    private void onTeamChangedInGame() {
        if (!GameManager.get().isInGame()) {
            return;
        }

        if (getStandingTeamCount() <= 1) {
            GameManager.get().checkIfGameShouldEnd();
        }
    }

    public int getStandingTeamCount() {
        return teamData.getTotalStandingPlayerCount();
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
        } else if (targetTeam.getTeamMembers().size() >= this.teamSize) { // 队伍满员
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", targetTeamId).withStyle(ChatFormatting.RED));
            return;
        }

        if (!request || targetTeam.getTeamMemberCount() == 0) { // 空队伍不用申请
            // 新建 GamePlayer
            int newPlayerId = teamData.generateNextPlayerId();
            if (newPlayerId < 1) { // 达到人数上限
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit", playerLimit).withStyle(ChatFormatting.RED));
                return;
            }
            GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), newPlayerId, false, targetTeam);
            if (teamData.addPlayerToTeam(gamePlayer, targetTeam)) {
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
                notifyPlayerJoinTeam(gamePlayer); // 通知队伍成员有新玩家加入
                return;
            }
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
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        }

        forceEliminatePlayerFromTeam(player); // 游戏进行时生效，退出即被淘汰，不在游戏运行时则自动跳过

        if (removePlayerFromTeam(player.getUUID())) { // 不在游戏时生效，手动离开当前队伍
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.GREEN));
        }
    }

    /**
     * 在游戏中强制淘汰玩家，不包含发送系统消息
     * 成功淘汰后传送回大厅
     */
    public boolean forceEliminatePlayerSilence(GamePlayer gamePlayer) {
        if (!GameManager.get().isInGame()) {
            return false;
        }

        if (teamData.eliminatePlayer(gamePlayer)) {
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null) {
                    GameManager.get().teleportToLobby(player); // 强制淘汰后传送回大厅
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 在游戏中强制淘汰玩家并向队友发送消息
     * 传送被淘汰的玩家至大厅
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
        if (!gameTeam.isTeamAlive()) {
            if (serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            }
            BattleRoyale.LOGGER.info("Team {} has been eliminated for no standing player", gameTeam.getGameTeamId());
        }
        GameManager.get().teleportToLobby(player); // 强制淘汰后传送回大厅
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
        } else if (senderGamePlayer.getTeam().getTeamMemberCount() >= this.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full").withStyle(ChatFormatting.RED));
            return;
        } else if (teamData.getGamePlayerByUUID(targetPlayer.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_already_in_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        int teamId = senderGamePlayer.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + getExpireTimeMillis();
        String targetName = targetPlayer.getName().getString();
        pendingInvites.put(sender.getUUID(), new TeamInvite(targetPlayer.getUUID(), targetName, teamId, expiryTime));
        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.invite_sent", targetName).withStyle(ChatFormatting.GREEN));

        String senderName = sender.getName().getString();
        MutableComponent message = Component.translatable("battleroyale.message.invite_received", senderName, teamId);
        String acceptCommand = TeamCommand.acceptInviteCommandString(senderName);
        String declineCommand = TeamCommand.declineInviteCommandString(senderName);
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
        } else if (targetTeam.getTeamMembers().size() >= this.teamSize) { // 目标队伍满员
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
        } else if (targetGamePlayer.getTeam().getTeamMemberCount() >= teamSize) { // 对方队伍已满员
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        } else if (!targetGamePlayer.isLeader()) { // 对方不是队长
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_actual_leader", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        GameTeam gameTeam = targetGamePlayer.getTeam();
        int targetTeamId = gameTeam.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + getExpireTimeMillis();

        pendingRequests.put(sender.getUUID(), new TeamRequest(targetGamePlayer.getPlayerUUID(), targetGamePlayer.getPlayerName(), targetTeamId, expiryTime));
        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.request_sent", targetTeamId).withStyle(ChatFormatting.GREEN));

        String senderName = sender.getName().getString();
        MutableComponent message = Component.translatable("battleroyale.message.request_received", senderName);
        String acceptCommand = TeamCommand.acceptRequestCommandString(senderName);
        String declineCommand = TeamCommand.declineRequestCommandString(senderName);
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
        if (targetTeam.getTeamMembers().size() >= this.teamSize) { // 检查目标队伍是否满员
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

        return teamData.removePlayer(playerId);
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
        GameTeam newTeam = new GameTeam(teamId, TEAM_COLORS[teamId % TEAM_COLORS.length]);
        if (!teamData.addGameTeam(newTeam)) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            return false;
        }
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), newPlayerId, false, newTeam);
        if (teamData.addPlayerToTeam(gamePlayer, newTeam)) {
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
        if (teamData.getTotalPlayerCount() >= playerLimit) {
            return -1;
        }

        // 寻找已有的未满员队伍
        List<Integer> idList = new ArrayList<>();
        for (GameTeam team : teamData.getGameTeamsList()) {
            if (team.getTeamMembers().size() < this.teamSize) {
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
            forceEliminatePlayerFromTeam(player); // 强制淘汰包含传送
        } else if (GameManager.get().teleportToLobby(player)) { // 传送，且传送成功
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.teleported_to_lobby").withStyle(ChatFormatting.GREEN));
        } else {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }

    public List<GameTeam> getGameTeamsList() {
        return teamData.getGameTeamsList();
    }

    public @Nullable GamePlayer getGamePlayerByUUID(UUID playerId) {
        return teamData.getGamePlayerByUUID(playerId);
    }

    public List<GamePlayer> getGamePlayersList() {
        return teamData.getGamePlayersList();
    }

    public List<GamePlayer> getStandingGamePlayersList() {
        return teamData.getStandingGamePlayersList();
    }

    public boolean hasStandingGamePlayer(UUID id) { return teamData.hasStandingGamePlayer(id); }

    public int getTotalMembers() {
        return teamData.getTotalPlayerCount();
    }

    private void clearTeamInfo() {
        if (!keepTeamAfterGame || teamData.getMaxPlayersLimit() > this.playerLimit) {
            teamData.clear(playerLimit);
            pendingInvites.clear();
            pendingRequests.clear();
        }
    }

    private boolean hasEnoughPlayerTeamToStart() {
        return hasEnoughPlayerToStart() && hasEnoughTeamToStart();
    }

    private boolean hasEnoughPlayerToStart() {
        int totalPlayerAndBots = getTotalMembers();
        return totalPlayerAndBots > 1 || (totalPlayerAndBots == 1 && aiEnemy);
    }

    private boolean hasEnoughTeamToStart() {
        int totalTeamCount = teamData.getTotalTeamCount();
        return totalTeamCount > 1 || (totalTeamCount == 1 && aiEnemy);
    }
}