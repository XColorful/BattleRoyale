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
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
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

    private final TeamData teamData = new TeamData();

    private record TeamInvite(UUID senderUUID, int teamId, long expiryTime) {}
    private final Map<UUID, TeamInvite> pendingInvites = new HashMap<>();
    private record TeamRequest(UUID requisterUUID, int requiestedTeamId, long expireTime) {}
    private final Map<UUID, TeamRequest> pendingRequests = new HashMap<>();

    private static final String[] TEAM_COLORS = {
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
            BattleRoyale.LOGGER.info("AutoJoinGame is disabled, players must manually join");
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
        if (!hasEnoughPlayerTeamToStart()) { // init之后可能都退出了队伍
            return false;
        }
        // TODO 处理人机填充
        teamData.startGame(); // 调用startGame，它会内部锁定数据
        return true;
    }

    @Override
    public void stopGame(ServerLevel serverLevel) {
        clearTeamInfo();
        this.prepared = false;
        this.ready = false;
        BattleRoyale.LOGGER.info("TeamManager stopped, clear all team info");
    }

    public void onPlayerDeath(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            BattleRoyale.LOGGER.info("onPlayerDeath: GamePlayer not found for UUID: {}, skipped", player.getUUID());
            return;
        }
        gamePlayer.setAlive(false); // GamePlayer会自动更新eliminated

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam == null) {
            BattleRoyale.LOGGER.warn("玩家 {} (UUID: {}) 死亡但未加入任何队伍。", player.getName().getString(), player.getUUID());
            return;
        }
        if (!gameTeam.isTeamAlive()) {
            BattleRoyale.LOGGER.info("Team {} has been eliminated", gameTeam.getGameTeamId());
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            }
            onTeamChangedInGame();
        }
    }

    private void onTeamChangedInGame() {
        if (!GameManager.get().isInGame()) {
            return;
        }

        if (getStandingTeamCount() <= 1) {
            GameManager.get().checkIfGameShouldEnd();
        }
    }

    private int getStandingTeamCount() {
        return teamData.getTotalStandingPlayerCount();
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) { // 登录的玩家已经在队伍信息里
            if (GameManager.get().isInGame() && gamePlayer.isEliminated()) {
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            }
            return;
        }

        if (autoJoinGame && !GameManager.get().isInGame()) {
            joinTeam(player);
        }
    }

    public void onPlayerLoggedOut(ServerPlayer player) {
        if (!GameManager.get().isInGame()) { // 没开始游戏就直接踢了
            removePlayerFromTeam(player.getUUID());
        }
    }

    public boolean isPlayerLeader(UUID playerUUID) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) { // 非游戏玩家
            return false;
        }
        GameTeam gameTeam = gamePlayer.getTeam();
        return gameTeam.isLeader(playerUUID);
    }

    public void forceJoinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (teamData.getGamePlayerByUUID(player.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
            removePlayerFromTeam(player.getUUID());
        }

        addPlayerToTeam(player, true); // forceJoinTeam强制加入
    }

    /**
     * 不强制加入队伍
     * @param player 需要加入队伍的玩家
     */
    public void joinTeam(ServerPlayer player) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (teamData.getGamePlayerByUUID(player.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
            removePlayerFromTeam(player.getUUID());
        }

        addPlayerToTeam(player, false); // 不强制join
    }

    /**
     * 不强制加入队伍
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    public void joinTeamSpecific(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        if (teamData.getGamePlayerByUUID(player.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
            removePlayerFromTeam(player.getUUID());
        }

        addPlayerToTeam(player, teamId, false);
    }

    /**
     * 非 force 则改为 request 申请
     * @param player 需要加入队伍的玩家
     */
    public void addPlayerToTeam(ServerPlayer player, boolean force) {
        UUID playerId = player.getUUID();
        if (teamData.getGamePlayerByUUID(playerId) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.YELLOW));
            BattleRoyale.LOGGER.info("Player (UUID: {}) is already in GamePlayer, skipped adding to team", playerId);
            return;
        }

        if (teamData.getTotalPlayerCount() >= playerLimit) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit", playerLimit).withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Failed to add player {}: player limit reached ({})", player.getName().getString(), playerLimit);
            return;
        }

        GameTeam targetTeam = findOrCreateTeamToJoin();
        if (targetTeam == null) {
            BattleRoyale.LOGGER.error("Failed to find or create a team for player {}", player.getName().getString());
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), teamData.generateNextPlayerId(), targetTeam.getGameTeamColor(), false, targetTeam);
        teamData.addPlayerToTeam(gamePlayer, targetTeam);
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
        BattleRoyale.LOGGER.info("Player {} (UUID: {}) joined team {}", player.getName().getString(), playerId, targetTeam.getGameTeamId());
    }

    /**
     * 非 force 则改为 request 申请
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    public void addPlayerToTeam(ServerPlayer player, int teamId, boolean force) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Player {} attempted to join team {} while game is in progress", player.getName().getString(), teamId);
            return;
        }

        GameTeam targetTeam = teamData.getGameTeamById(teamId);
        if (targetTeam == null) {
            // 尝试创建一个新队伍并加入
            createNewTeamToJoin(player);
            return;
        }
        if (targetTeam.getTeamMembers().size() >= this.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", teamId).withStyle(ChatFormatting.RED));
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

    public void leaveTeam(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.YELLOW));
            BattleRoyale.LOGGER.info("Player {} (UUID: {}) attempt to leave team but already not in team", player.getName().getString(), player.getUUID());
            return;
        }
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            forceRemovePlayerFromTeam(player); // 游戏进行时退出即被淘汰
            BattleRoyale.LOGGER.info("Player {} (UUID: {}) leave team in game, force removed from game", player.getName().getString(), player.getUUID());
            return;
        }
        removePlayerFromTeam(player.getUUID());
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.GREEN));
        BattleRoyale.LOGGER.info("Player {} (UUID: {}) leaved team", player.getName().getString(), player.getUUID());
    }

    public void forceRemovePlayerFromTeam(ServerPlayer player) {
        GamePlayer gamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("Attempt to force remove player {} (UUID: {}), but already not in team", player.getName().getString(), player.getUUID());
            return;
        }

        teamData.eliminatePlayer(player.getUUID()); // 使用eliminatePlayer进行淘汰

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.forced_elimination", player.getName()).withStyle(ChatFormatting.RED));
        }

        BattleRoyale.LOGGER.info("Player {} (UUID: {}) has been force removed", player.getName().getString(), player.getUUID());

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam != null) {
            if (gameTeam.isTeamAlive()) {
                BattleRoyale.LOGGER.info("Team {} has been eliminated for no standing player", gameTeam.getGameTeamId());
                if (serverLevel != null) {
                    ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                }
            }
        }
        onTeamChangedInGame();
    }

    public void kickPlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(sender.getUUID());
        GamePlayer targetGamePlayer = teamData.getGamePlayerByUUID(targetPlayer.getUUID());

        if (senderGamePlayer == null || !senderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }

        if (targetGamePlayer == null || targetGamePlayer.getTeam() == null || !senderGamePlayer.getTeam().equals(targetGamePlayer.getTeam())) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        removePlayerFromTeam(targetPlayer.getUUID());
        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_kicked_from_team", targetPlayer.getName()).withStyle(ChatFormatting.GREEN));
        BattleRoyale.LOGGER.info("{} (UUID: {}) kicked {} (UUID: {}) from team {}", sender.getName().getString(), sender.getUUID(), targetPlayer.getName().getString(), targetPlayer.getUUID(), senderGamePlayer.getGameTeamId());
    }

    public void invitePlayer(ServerPlayer sender, ServerPlayer targetPlayer) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(sender.getUUID());

        if (senderGamePlayer == null || !senderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }
        if (senderGamePlayer.getTeam().getTeamMemberCount() >= this.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full").withStyle(ChatFormatting.RED));
            return;
        }
        if (teamData.getGamePlayerByUUID(targetPlayer.getUUID()) != null) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.player_already_in_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }
        if (pendingInvites.containsKey(targetPlayer.getUUID())) {
            TeamInvite existingInvite = pendingInvites.get(targetPlayer.getUUID());
            if (existingInvite.teamId() == senderGamePlayer.getGameTeamId()) {
                ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.invite_already_sent", targetPlayer.getName()).withStyle(ChatFormatting.YELLOW));
                return;
            }
        }

        int teamId = senderGamePlayer.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + 300 * 1000;
        pendingInvites.put(targetPlayer.getUUID(), new TeamInvite(sender.getUUID(), teamId, expiryTime));

        ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.invite_sent", targetPlayer.getName()).withStyle(ChatFormatting.GREEN));

        MutableComponent message = Component.translatable("battleroyale.message.invite_received", teamId);
        MutableComponent acceptButton = Component.translatable("battleroyale.message.accept").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battleroyale team accept " + teamId))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("battleroyale.message.team", teamId))));
        MutableComponent declineButton = Component.translatable("battleroyale.message.decline").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battleroyale team decline " + teamId)));

        message.append(" ").append(acceptButton).append(" ").append(declineButton);
        ChatUtils.sendClickableMessageToPlayer(targetPlayer, message);
        BattleRoyale.LOGGER.info("Player {} (UUID: {}) invite player {} (UUID: {}) to join team {}", sender.getName().getString(), sender.getUUID(), targetPlayer.getName().getString(), targetPlayer.getUUID(), teamId);
    }

    public void acceptInvite(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamInvite invite = pendingInvites.get(player.getUUID());
        if (invite == null || invite.teamId() != teamId) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            pendingInvites.remove(player.getUUID());
            return;
        }
        if(invite.expiryTime() < System.currentTimeMillis()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            pendingInvites.remove(player.getUUID());
            return;
        }

        pendingInvites.remove(player.getUUID());

        GamePlayer existingGamePlayer = teamData.getGamePlayerByUUID(player.getUUID());
        if (existingGamePlayer != null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.RED));
            return;
        }

        GameTeam targetTeam = teamData.getGameTeamById(teamId);
        if (targetTeam == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_does_not_exist", teamId).withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("玩家 {} 尝试加入的队伍 {} 不存在", player.getName().getString(), teamId);
            return;
        }
        if (targetTeam.getTeamMembers().size() >= this.teamSize) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", teamId).withStyle(ChatFormatting.RED));
            BattleRoyale.LOGGER.warn("玩家 {} 尝试加入的队伍 {} 已满", player.getName().getString(), teamId);
            return;
        }

        addPlayerToTeam(player, teamId, true); // 接受队长的邀请
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.invite_accepted", teamId).withStyle(ChatFormatting.GREEN));

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ServerPlayer senderPlayer = serverLevel.getServer().getPlayerList().getPlayer(invite.senderUUID());
            if (senderPlayer != null && !senderPlayer.getUUID().equals(player.getUUID())) {
                ChatUtils.sendTranslatableMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_accepted_invite", player.getName(), teamId).withStyle(ChatFormatting.GREEN));
            }
            for (GamePlayer member : targetTeam.getTeamMembers()) {
                if (!member.getPlayerUUID().equals(player.getUUID()) && (senderPlayer == null || !member.getPlayerUUID().equals(senderPlayer.getUUID()))) {
                    ServerPlayer teamMember = serverLevel.getServer().getPlayerList().getPlayer(member.getPlayerUUID());
                    if (teamMember != null) {
                        ChatUtils.sendTranslatableMessageToPlayer(teamMember, Component.translatable("battleroyale.message.player_accepted_invite", player.getName(), teamId).withStyle(ChatFormatting.GREEN));
                    }
                }
            }
        }
        BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 接受了队伍 {} 的邀请", player.getName().getString(), player.getUUID(), teamId);
    }

    public void declineInvite(ServerPlayer player, int teamId) {
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return;
        }

        TeamInvite invite = pendingInvites.get(player.getUUID());
        if (invite == null || invite.teamId() != teamId) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        }
        if (invite.expiryTime() < System.currentTimeMillis()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            pendingInvites.remove(player.getUUID());
            return;
        }

        pendingInvites.remove(player.getUUID());
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.invite_declined", teamId).withStyle(ChatFormatting.YELLOW));

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }
        ServerPlayer senderPlayer = serverLevel.getServer().getPlayerList().getPlayer(invite.senderUUID());
        if (senderPlayer == null) {
            BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 拒绝了来自已失效发送者的邀请", player.getName().getString(), player.getUUID());
            return;
        }
        GamePlayer senderGamePlayer = teamData.getGamePlayerByUUID(senderPlayer.getUUID());
        if (senderGamePlayer == null) {
            BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 拒绝了来自已失效发送者的邀请", player.getName().getString(), player.getUUID());
            return;
        }
        GameTeam senderTeam = senderGamePlayer.getTeam();
        if (senderTeam != null && senderTeam.getGameTeamId() == teamId && senderGamePlayer.isLeader()) {
            ChatUtils.sendTranslatableMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_declined_invite", player.getName(), teamId).withStyle(ChatFormatting.YELLOW));
            BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 拒绝了队伍 {} 的邀请", player.getName().getString(), player.getUUID(), teamId);
        }
        BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 拒绝了来自已失效发送者的邀请", player.getName().getString(), player.getUUID());
    }

    public void removePlayerFromTeam(@NotNull UUID playerId) {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.warn("游戏正在进行中，尝试直接从队伍中完全移除玩家 {}。操作已跳过。请使用 'forceRemovePlayerFromTeam' 来淘汰玩家。", playerId);
            return;
        }
        GamePlayer removedPlayer = teamData.removePlayer(playerId);
        if (removedPlayer == null) {
            return;
        }
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel != null) {
            ServerPlayer onlinePlayer = serverLevel.getServer().getPlayerList().getPlayer(playerId);
            if (onlinePlayer != null) {
                ChatUtils.sendTranslatableMessageToPlayer(onlinePlayer, Component.translatable("battleroyale.message.removed_from_team").withStyle(ChatFormatting.YELLOW));
            }
        }
        BattleRoyale.LOGGER.info("玩家 {} (UUID: {}) 从队伍中移除。", removedPlayer.getPlayerName(), playerId);
    }

    private void createNewTeamToJoin(ServerPlayer player) {
        if (teamData.getTotalPlayerCount() >= playerLimit) {
            return;
        }
        int newTeamId = teamData.generateNextTeamId();
        int newPlayerId = teamData.generateNextPlayerId();
        if (newTeamId < 1 || newPlayerId < 1) {
            return;
        }
        String teamColor = TEAM_COLORS[newTeamId % TEAM_COLORS.length];
        GameTeam newTeam = new GameTeam(newTeamId, teamColor);
        teamData.addGameTeam(newTeam);
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), player.getName().getString(), newPlayerId, teamColor, false, newTeam);
        teamData.addPlayerToTeam(gamePlayer, newTeam);
        ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", newTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
    }

    private @Nullable GameTeam findOrCreateTeamToJoin() {
        for (GameTeam team : teamData.getGameTeamsList()) {
            if (team.getTeamMembers().size() < this.teamSize) {
                return team;
            }
        }

        if (teamData.getTotalPlayerCount() < playerLimit) {
            int newTeamId = teamData.generateNextTeamId();
            if (newTeamId < 1) {
                return null;
            }
            String teamColor = TEAM_COLORS[newTeamId % TEAM_COLORS.length];
            GameTeam newTeam = new GameTeam(newTeamId, teamColor);
            teamData.addGameTeam(newTeam);
            return newTeam;
        }

        BattleRoyale.LOGGER.info("无法创建新队伍，已达到总玩家上限或无法容纳更多队伍。");
        return null;
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

    public int getTotalMembers() {
        return teamData.getTotalPlayerCount();
    }

    private void clearTeamInfo() {
        teamData.clear(playerLimit);
        pendingInvites.clear();
        pendingRequests.clear();
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