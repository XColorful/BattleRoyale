package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.game.team.InvitePlayerCompleteEvent;
import xiao.battleroyale.api.event.game.team.InvitePlayerEvent;
import xiao.battleroyale.api.event.game.team.RequestPlayerCompleteEvent;
import xiao.battleroyale.api.event.game.team.RequestPlayerEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TeamExternal {

    /**
     * 玩家加入游戏，优先创建队伍，无法创建队伍则发送申请
     * @param player 需要加入队伍的玩家
     */
    protected static void joinTeam(TeamManager teamManager, ServerPlayer player) {
        if (teamManager.removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        int newTeamId = teamManager.teamData.generateNextTeamId();
        if (TeamManagement.createNewTeamAndJoin(teamManager, player, newTeamId)) { // 默认尝试创建队伍
            return;
        }

        TeamManagement.addPlayerToTeamInternal(teamManager, player, teamManager.findNotFullTeamId(), true); // 尝试申请加入
    }

    /**
     * 玩家尝试创建一个指定的队伍 (已存在则改为申请)。
     * @param player 需要加入队伍的玩家
     * @param teamId 加入队伍的 teamId
     */
    protected static void joinTeamSpecific(TeamManager teamManager, ServerPlayer player, int teamId) {
        if (teamManager.removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        if (TeamManagement.createNewTeamAndJoin(teamManager, player, teamId)) { // 手动加入队伍
            return;
        }

        TeamManagement.addPlayerToTeamInternal(teamManager, player, teamId, true); // 无法创建则尝试申请加入
    }

    protected static void kickPlayer(TeamManager teamManager, ServerPlayer sender, ServerPlayer targetPlayer) {
        if (sender == null) {
            return;
        } else if (targetPlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamManager.teamData.getGamePlayerByUUID(sender.getUUID());
        GamePlayer targetGamePlayer = teamManager.teamData.getGamePlayerByUUID(targetPlayer.getUUID());

        if (senderGamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!senderGamePlayer.isLeader()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer == null || targetGamePlayer.getGameTeamId() != senderGamePlayer.getGameTeamId()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        if (teamManager.removePlayerFromTeam(targetPlayer.getUUID())) { // 手动踢人
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_kicked_from_team", targetPlayer.getName()).withStyle(ChatFormatting.YELLOW));
            ChatUtils.sendComponentMessageToPlayer(targetPlayer, Component.translatable("battleroyale.message.kicked_by_leader", sender.getName()).withStyle(ChatFormatting.RED));
        }
    }

    public static void invitePlayer(TeamManager teamManager, ServerPlayer sender, ServerPlayer targetPlayer) {
        GamePlayer senderGamePlayer = teamManager.teamData.getGamePlayerByUUID(sender.getUUID());
        if (senderGamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!senderGamePlayer.isLeader()) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        } else if (senderGamePlayer.getTeam().getTeamMemberCount() >= teamManager.teamConfig.teamSize) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full").withStyle(ChatFormatting.RED));
            return;
        } else if (teamManager.teamData.getGamePlayerByUUID(targetPlayer.getUUID()) != null) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_already_in_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        if (EventPoster.postEvent(new InvitePlayerEvent(BattleRoyale.getGameManager(), senderGamePlayer, sender, targetPlayer))) {
            BattleRoyale.LOGGER.debug("InvitePlayerEvent canceled, skipped invitePlayer ({} to {})", senderGamePlayer.getNameWithId(), targetPlayer.getName().getString());
            return;
        }

        int teamId = senderGamePlayer.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + teamManager.teamConfig.teamMsgExpireTimeMillis;
        String targetName = targetPlayer.getName().getString();
        teamManager.pendingInvites.put(sender.getUUID(), new TeamManager.TeamInvite(targetPlayer.getUUID(), targetName, teamId, expiryTime));
        ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.invite_sent", targetName).withStyle(ChatFormatting.GREEN));

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

    public static void acceptInvite(TeamManager teamManager, ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null || player == null) {
            return;
        } else if (senderPlayer == null || !TeamUtils.isPlayerLeader(teamManager, senderPlayer.getUUID())) { // 玩家未加载或不是队长
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        }
        UUID senderUUID = senderPlayer.getUUID();
        UUID playerUUID = player.getUUID();
        TeamManager.TeamInvite invite = teamManager.pendingInvites.get(senderUUID);
        if (invite == null || !invite.targetPlayerUUID().equals(playerUUID)) { // 已经改为向其他人发的邀请
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        } else if (invite.expiryTime() < System.currentTimeMillis()) { // 邀请是否过期
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            teamManager.pendingInvites.remove(senderUUID);
            return;
        } else if (teamManager.teamData.getGamePlayerByUUID(playerUUID) != null) { // 检查接收者是否已在队伍中
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.RED));
            teamManager.pendingInvites.remove(senderUUID);
            return;
        }

        if (EventPoster.postEvent(new InvitePlayerCompleteEvent(gameManager, senderPlayer, player, true))) {
            BattleRoyale.LOGGER.debug("InvitePlayerCompleteEvent canceled, skipped acceptInvite ({} to {})", senderPlayer.getName().getString(), player.getName().getString());
            return;
        }

        GameTeam targetTeam = teamManager.teamData.getGameTeamById(invite.teamId());
        String playerName = player.getName().getString();
        if (targetTeam == null) { // 目标队伍不存在
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.team_does_not_exist", invite.teamId()).withStyle(ChatFormatting.RED));
            teamManager.pendingInvites.remove(senderUUID);
            return;
        } else if (targetTeam.getTeamMembers().size() >= teamManager.teamConfig.teamSize) { // 目标队伍满员
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", invite.teamId()).withStyle(ChatFormatting.RED));
            teamManager.pendingInvites.remove(senderUUID);
            return;
        }

        teamManager.pendingInvites.remove(senderUUID);
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.invite_accepted", invite.teamId()).withStyle(ChatFormatting.GREEN));
        ChatUtils.sendComponentMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_accept_request", playerName).withStyle(ChatFormatting.GREEN));
        TeamManagement.addPlayerToTeamInternal(teamManager, player, invite.teamId(), false); // 同意邀请，强制加入
    }

    public static void declineInvite(TeamManager teamManager, ServerPlayer player, ServerPlayer senderPlayer) { // 接收者，发送者名称
        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null || player == null) {
            return;
        } else if (senderPlayer == null || !TeamUtils.isPlayerLeader(teamManager, senderPlayer.getUUID())) { // 玩家未加载或不是队长
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        }
        UUID senderUUID = senderPlayer.getUUID();
        UUID playerUUID = player.getUUID();
        TeamManager.TeamInvite invite = teamManager.pendingInvites.get(senderUUID);
        if (invite == null || !invite.targetPlayerUUID().equals(playerUUID)) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_valid_invite").withStyle(ChatFormatting.RED));
            return;
        } else if (invite.expiryTime() < System.currentTimeMillis()) { // 邀请是否过期
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.expired_invite").withStyle(ChatFormatting.RED));
            teamManager.pendingInvites.remove(senderUUID);
            return;
        }

        if (EventPoster.postEvent(new InvitePlayerCompleteEvent(gameManager, senderPlayer, player, false))) {
            BattleRoyale.LOGGER.debug("InvitePlayerCompleteEvent canceled, skipped declineInvite ({} to {})", senderPlayer.getName().getString(), player.getName().getString());
            return;
        }

        teamManager.pendingInvites.remove(senderUUID);
        String playerName = player.getName().getString();
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.invite_declined", invite.teamId()).withStyle(ChatFormatting.YELLOW));
        ChatUtils.sendComponentMessageToPlayer(senderPlayer, Component.translatable("battleroyale.message.player_declined_invite", playerName).withStyle(ChatFormatting.RED));
    }

    public static void requestPlayer(TeamManager teamManager, ServerPlayer sender, ServerPlayer targetPlayer) { // 申请者，目标玩家
        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null || sender == null) {
            return;
        } else if (targetPlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer senderGamePlayer = teamManager.teamData.getGamePlayerByUUID(sender.getUUID());
        GamePlayer targetGamePlayer = teamManager.teamData.getGamePlayerByUUID(targetPlayer.getUUID());
        if (senderGamePlayer != null) { // 申请者已在队伍里
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer == null) { // 对方不在队伍里
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.target_not_in_a_team", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        } else if (targetGamePlayer.getTeam().getTeamMemberCount() >= teamManager.teamConfig.teamSize) { // 对方队伍已满员
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.team_full", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        } else if (!targetGamePlayer.isLeader()) { // 对方不是队长
            ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.player_not_actual_leader", targetPlayer.getName()).withStyle(ChatFormatting.RED));
            return;
        }

        if (EventPoster.postEvent(new RequestPlayerEvent(gameManager, sender, targetGamePlayer, targetPlayer))) {
            BattleRoyale.LOGGER.debug("RequestPlayerEvent canceled, skipped requestPlayer ({} to {})", sender.getName().getString(), targetGamePlayer.getNameWithId());
            return;
        }

        GameTeam gameTeam = targetGamePlayer.getTeam();
        int targetTeamId = gameTeam.getGameTeamId();
        long expiryTime = System.currentTimeMillis() + teamManager.teamConfig.teamMsgExpireTimeMillis;

        teamManager.pendingRequests.put(sender.getUUID(), new TeamManager.TeamRequest(targetGamePlayer.getPlayerUUID(), targetGamePlayer.getPlayerName(), targetTeamId, expiryTime));
        ChatUtils.sendComponentMessageToPlayer(sender, Component.translatable("battleroyale.message.request_sent", targetTeamId).withStyle(ChatFormatting.GREEN));

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

    public static void acceptRequest(TeamManager teamManager, ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null || teamLeader == null) {
            return;
        } else if (requesterPlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer leaderGamePlayer = teamManager.teamData.getGamePlayerByUUID(teamLeader.getUUID());

        if (leaderGamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!leaderGamePlayer.isLeader()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }
        UUID requesterUUID = requesterPlayer.getUUID();
        String requesterName = requesterPlayer.getName().getString();
        TeamManager.TeamRequest request = teamManager.pendingRequests.get(requesterUUID);

        if (request == null || request.requestedTeamId() != leaderGamePlayer.getGameTeamId() // 是否是发送给队长的
                || !request.targetTeamLeaderUUID().equals(teamLeader.getUUID())) { // 已经改为向其他人发的邀请
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.no_valid_request").withStyle(ChatFormatting.RED));
            return;
        } else if (request.expireTime() < System.currentTimeMillis()) { // 检查请求是否过期
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.expired_request").withStyle(ChatFormatting.RED));
            teamManager.pendingRequests.remove(requesterUUID);
            return;
        } else if (teamManager.teamData.getGamePlayerByUUID(requesterUUID) != null) { // 检查申请者是否已在队伍中
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_already_in_team", requesterName).withStyle(ChatFormatting.RED));
            teamManager.pendingRequests.remove(requesterUUID);
            return;
        }
        GameTeam targetTeam = leaderGamePlayer.getTeam();
        if (targetTeam.getTeamMembers().size() >= teamManager.teamConfig.teamSize) { // 检查目标队伍是否满员
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.team_full", targetTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            teamManager.pendingRequests.remove(requesterUUID);
            return;
        }

        if (EventPoster.postEvent(new RequestPlayerCompleteEvent(gameManager, requesterPlayer, teamLeader, true))) {
            BattleRoyale.LOGGER.debug("RequestPlayerCompleteEvent canceled, skipped acceptRequest ({} to {})", requesterPlayer, teamLeader);
            return;
        }

        teamManager.pendingRequests.remove(requesterUUID);
        ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.request_accepted", requesterName).withStyle(ChatFormatting.GREEN));
        ChatUtils.sendComponentMessageToPlayer(requesterPlayer, Component.translatable("battleroyale.message.player_accept_request", teamLeader.getName().getString()).withStyle(ChatFormatting.GREEN));
        TeamManagement.addPlayerToTeamInternal(teamManager, requesterPlayer, targetTeam.getGameTeamId(), false); // 同意申请，强制加入
    }

    public static void declineRequest(TeamManager teamManager, ServerPlayer teamLeader, ServerPlayer requesterPlayer) { // 队长，申请者名称
        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null || teamLeader == null) {
            return;
        } else if (requesterPlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.player_not_found", "").withStyle(ChatFormatting.RED));
            return;
        }

        GamePlayer leaderGamePlayer = teamManager.teamData.getGamePlayerByUUID(teamLeader.getUUID());

        if (leaderGamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        } else if (!leaderGamePlayer.isLeader()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.not_team_leader").withStyle(ChatFormatting.RED));
            return;
        }

        UUID requesterUUID = requesterPlayer.getUUID();
        String requesterName = requesterPlayer.getName().getString();
        TeamManager.TeamRequest request = teamManager.pendingRequests.get(requesterUUID);

        if (request == null || !request.targetTeamLeaderUUID().equals(teamLeader.getUUID())
                || request.requestedTeamId() != leaderGamePlayer.getGameTeamId()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.no_valid_request").withStyle(ChatFormatting.RED));
            return;
        } else if (request.expireTime() < System.currentTimeMillis()) {
            ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.expired_request").withStyle(ChatFormatting.RED));
            teamManager.pendingRequests.remove(requesterUUID);
            return;
        }

        if (EventPoster.postEvent(new RequestPlayerCompleteEvent(gameManager, requesterPlayer, teamLeader, false))) {
            BattleRoyale.LOGGER.debug("RequestPlayerCompleteEvent canceled, skipped declineRequest ({} to {})", requesterPlayer, teamLeader);
            return;
        }

        teamManager.pendingRequests.remove(requesterUUID);
        ChatUtils.sendComponentMessageToPlayer(teamLeader, Component.translatable("battleroyale.message.request_declined", requesterName).withStyle(ChatFormatting.YELLOW));
        ChatUtils.sendComponentMessageToPlayer(requesterPlayer, Component.translatable("battleroyale.message.player_declined_request", teamLeader.getName().getString()).withStyle(ChatFormatting.RED));
    }

    public static boolean leaveTeam(TeamManager teamManager, @NotNull ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        GamePlayer gamePlayer = teamManager.teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return false;
        }

        teamManager.forceEliminatePlayerFromTeam(player); // 游戏进行时生效，退出即被淘汰，不在游戏运行时则自动跳过

        // ↑在因为forceEliminatePlayerFromTeam而结束游戏后，就不在游戏中
        if (teamManager.removePlayerFromTeam(playerUUID)) { // 不在游戏时生效，手动离开当前队伍
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.GREEN));
        }
        return teamManager.getGamePlayerByUUID(playerUUID) == null;
    }

    public static boolean addToTeam(TeamManager teamManager, @Nullable CommandSourceStack source, LivingEntity player, int teamId) {
        GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());

        // 已经在队伍里
        if (gamePlayer != null) {
            if (gamePlayer.getGameTeamId() == teamId) { // 已经在目标队伍里
                if (source != null) source.sendFailure(Component.translatable("battleroyale.message.player_already_in_team", player.getName()));
                return false;
            }
            // 先退队
            if (!teamManager.removePlayerFromTeam(gamePlayer.getPlayerUUID())) { // 无法退队
                BattleRoyale.LOGGER.warn("AddToTeam: Failed to remove LivingEntity {} (GamePlayer {}) from team", player.getName().getString(), gamePlayer.getNameWithId());
                if (source != null) source.sendFailure(Component.translatable("battleroyale.message.not_added_to_team", player.getName(), teamId));
                return false;
            }
        }

        if (teamManager.teamData.getGameTeamById(teamId) == null) {
            TeamManagement.createNewTeamAndJoin(teamManager, player, teamId); // 手动强制新建队伍
        } else {
            TeamManagement.addPlayerToTeamInternal(teamManager, player, teamId, false); // 手动强制入队
        }

        gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
        boolean added = gamePlayer != null && gamePlayer.getGameTeamId() == teamId; // 判断是否是目标队伍的GamePlayer
        if (added) {
            if (source != null) {
                String playerName = gamePlayer.getPlayerName();
                int playerTeamId = gamePlayer.getGameTeamId();
                source.sendSuccess(() -> Component.translatable("battleroyale.message.added_to_team", playerName, playerTeamId), true);
            }
            return true;
        } else {
            if (source != null) source.sendFailure(Component.translatable("battleroyale.message.not_added_to_team", player.getName(), teamId));
            return false;
        }
    }

    public static int buildTeamForAll(TeamManager teamManager, @Nullable CommandSourceStack source, List<LivingEntity> players, int targetTeamSize, boolean forceRebuild) {
        int expectedBuildTotal = players.size();
        int buildTotal = 0;
        List<LivingEntity> pendingPlayers = new ArrayList<>();
        for (LivingEntity player : players) {
            GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
            // 不强制重建 + 已经在队伍里
            if (!forceRebuild && gamePlayer != null) {
                BattleRoyale.LOGGER.debug("BuildTeam: Skipped LivingEntity {} (GamePlayer {})", player.getName().getString(), gamePlayer.getNameWithId());
                continue;
            }
            // 强制重建
            if (gamePlayer != null) { // 先退之前队伍
                if (!teamManager.removePlayerFromTeam(gamePlayer.getPlayerUUID())) { // 无法退队
                    BattleRoyale.LOGGER.warn("BuildTeam: Failed to remove LivingEntity {} (GamePlayer {}) from team", player.getName().getString(), gamePlayer.getNameWithId());
                    continue;
                }
            }
            pendingPlayers.add(player);
        }
        int pendingBuildTotal = pendingPlayers.size();
        BattleRoyale.LOGGER.debug("BuildTeam: expected build for {} players, and {} pending to build team", expectedBuildTotal, pendingPlayers.size());
        if (pendingPlayers.isEmpty()) {
            if (source != null) {
                source.sendFailure(Component.translatable("battleroyale.message.no_player_pending_to_build_team"));
                source.sendFailure(Component.translatable("battleroyale.message.build_team_expected_pending_actual", expectedBuildTotal, pendingBuildTotal, 0));
            }
            return 0;
        }

        // 随机顺序，每次调用都重新组队
        Collections.shuffle(pendingPlayers, BattleRoyale.COMMON_RANDOM);

        // ----所有待处理玩家均为无队伍玩家----

        // ----开始组建队伍----

        int lastTeamId = 0;
        for (LivingEntity player : pendingPlayers) {
            // 无法创建新GamePlayer (已达到最大玩家)
            // 宣传招聘不保证没有内幕
//            if (teamManager.teamData.getTotalPlayerCount() < teamManager.teamData.getMaxPlayersLimit()) {
//                break;
//            }
            // 能拿到票一定能进场
            int playerSingleId = teamManager.teamData.generateNextPlayerId();
            if (playerSingleId < 1) {
                break;
            }

            @Nullable GameTeam lastGameTeam = teamManager.getGameTeamById(lastTeamId);
            int currentTeamLimit = Math.min(targetTeamSize, teamManager.teamData.getTeamSizeLimit()); // 实时获取队伍人数限制
            // 上一个队伍存在且未满员，优先加入上一个队伍
            int newTeamId = lastGameTeam != null && lastGameTeam.getTeamMemberCount() < currentTeamLimit
                    ? lastGameTeam.getGameTeamId()
                    : teamManager.teamData.generateNextTeamId(); // 其余情况准备创建新队伍
            // 无法创建新队伍
            if (newTeamId < 1) {
                break;
            }

            // 加入新队伍
            if (teamManager.getGameTeamById(newTeamId) != null) {
                TeamManagement.addPlayerToTeamInternal(teamManager, player, newTeamId, false); // 手动强制组建队伍
            } else {
                TeamManagement.createNewTeamAndJoin(teamManager, player, newTeamId); // 手动强制组建队伍
            }

            // 加入队伍失败则不视为已经无法加入
            GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
            if (gamePlayer != null) {
                lastTeamId = gamePlayer.getGameTeamId();
                if (lastTeamId == newTeamId) { // 按预期加入
                    BattleRoyale.LOGGER.debug("BuildTeam: build team for player {} (GamePlayer {})", player.getName().getString(), gamePlayer.getNameWithId());
                    buildTotal++;
                } else if (lastTeamId < 1) { // 队伍异常
                    BattleRoyale.LOGGER.warn("BuildTeam: Encountered an error (lastTeamId {} < 1) in BuildTeam for player {} (GamePlayer {}), this will not counted as a success build", lastTeamId, player.getName().getString(), gamePlayer.getNameWithId());
                } else {
                    BattleRoyale.LOGGER.warn("BuildTeam: build team for player {} (GamePlayer {}) but has an unexpected teamId (Expect: {}, Actual: {})", player.getName().getString(), gamePlayer.getNameWithId(), newTeamId, lastTeamId);
                    buildTotal++;
                }
            } else {
                BattleRoyale.LOGGER.debug("BuildTeam: Failed to build team for player {}", player.getName().getString());
                lastTeamId = 0;
            }
        }

        // ----组建完成----

        int actualBuildTotal = buildTotal;
        if (source != null) {
            Component statsComponent = Component.translatable("battleroyale.message.build_team_expected_pending_actual", expectedBuildTotal, pendingBuildTotal, actualBuildTotal);

            if (actualBuildTotal == expectedBuildTotal) { // 完美完成，全部组建队伍
                source.sendSuccess(() -> Component.translatable("battleroyale.message.build_team_success").withStyle(ChatFormatting.GREEN), true);
            } else if (actualBuildTotal > 0) { // 部分完成
                source.sendSuccess(() -> Component.translatable("battleroyale.message.build_team_success"), true);
                source.sendSuccess(() -> statsComponent, true);
            } else { // 没有组建队伍
                source.sendSuccess(() -> Component.translatable("battleroyale.message.build_team_failed").withStyle(ChatFormatting.RED), true);
                source.sendSuccess(() -> statsComponent, true);
            }
        }

        return actualBuildTotal;
    }
}
