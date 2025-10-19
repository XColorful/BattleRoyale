package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.event.handler.util.DelayedEvent;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * 该类仅用于抽离TeamManager的功能实现，简化TeamManager
 * 类似.h和.cpp的设计
 */
public class TeamManagement {

    /**
     * 玩家强制加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    protected static void forceJoinTeam(ServerPlayer player) {
        TeamManager teamManager = TeamManager.get();

        if (teamManager.removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        int newTeamId = teamManager.findNotFullTeamId();
        if (newTeamId > 0) { // 有未满员队伍
            TeamManagement.addPlayerToTeamInternal(player, teamManager.findNotFullTeamId(), false); // 直接强制加入
        } else {
            newTeamId = teamManager.teamData.generateNextTeamId();
            TeamManagement.createNewTeamAndJoin(player, newTeamId); // 无未满员队伍则创建队伍
        }
    }

    /**
     * 指定加入的队伍，不自动将申请的玩家离开队伍
     * @param player 需要加入队伍的玩家
     * @param targetTeamId 目标队伍的 ID
     * @param request 如果为 true，则尝试直接加入（跳过队长确认）；如果为 false，则当队伍有在线成员时发送申请。
     */
    protected static void addPlayerToTeamInternal(ServerPlayer player, int targetTeamId, boolean request) {
        TeamManager teamManager = TeamManager.get();
        GameManager gameManager = GameManager.get();

        UUID playerId = player.getUUID();
        GameTeam targetTeam = teamManager.teamData.getGameTeamById(targetTeamId);
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (targetTeam == null || serverLevel == null) { // 队伍不存在直接跳过
            return;
        } else if (teamManager.teamData.getGamePlayerByUUID(playerId) != null) { // 不自动离开队伍
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.YELLOW));
            return;
        } else if (targetTeam.getTeamMembers().size() >= teamManager.teamConfig.teamSize) { // 队伍满员
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.team_full", targetTeamId).withStyle(ChatFormatting.RED));
            return;
        }

        if (!request || targetTeam.getTeamMemberCount() == 0) { // 空队伍不用申请
            // 新建 GamePlayer
            int newPlayerId = teamManager.teamData.generateNextPlayerId();
            if (newPlayerId < 1) { // 达到人数上限
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit", teamManager.teamConfig.playerLimit).withStyle(ChatFormatting.RED));
                return;
            }
            String playerName = player.getName().getString();
            GamePlayer gamePlayer = new GamePlayer(player.getUUID(), playerName, newPlayerId, false, targetTeam);
            if (!teamManager.teamData.addPlayerToTeam(gamePlayer, targetTeam)) {
                BattleRoyale.LOGGER.debug("Failed to add player {} to team {}", playerName, targetTeamId);
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                return;
            }
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
            TeamNotification.notifyPlayerJoinTeam(gamePlayer, serverLevel); // 通知队伍成员有新玩家加入
            GameMessageManager.notifyTeamChange(targetTeam.getGameTeamId()); // 玩家加入队伍，通知更新队伍HUD

            // 加入原版Team
            if (gameManager.getGameEntry().buildVanillaTeam) {
                try {
                    Scoreboard scoreboard = serverLevel.getScoreboard();
                    PlayerTeam vanillaTeam;
                    if (targetTeam.getTeamMemberCount() == 1) { // 仅在GameTeam刚创建时移除同名队伍干扰
                        vanillaTeam = TeamUtils.getClearedVanillaTeam(scoreboard, gameManager.getGameEntry().hideVanillaTeamName, targetTeam);
                    } else {
                        vanillaTeam = TeamUtils.getOrCreateVanillaTeam(scoreboard, gameManager.getGameEntry().hideVanillaTeamName, targetTeam);
                    }
                    scoreboard.addPlayerToTeam(playerName, vanillaTeam);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Error in TeamManagement::addPlayerToTeamInternal, in build vanilla team: {}", e.getMessage());
                }
            }

        } else { // 改为发送邀请
            if (serverLevel.getPlayerByUUID(targetTeam.getLeaderUUID()) instanceof ServerPlayer targetPlayer) {
                teamManager.requestPlayer(player, targetPlayer);
            } else {
                BattleRoyale.LOGGER.warn("TeamManagement: team {} leader is not ServerPlayer, decline to add {} to team", targetTeam.getGameTeamId(), player.getName().getString());
            }
        }
    }

    /**
     * 清理掉离线GamePlayer，防止后续影响游戏结束的人数判定
     */
    protected static void removeOfflineGamePlayer(ServerLevel serverLevel) {
        TeamManager teamManager = TeamManager.get();
        List<GamePlayer> offlineGamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamManager.teamData.getGamePlayersList()) {
            if (!gamePlayer.isActiveEntity()) {
                offlineGamePlayers.add(gamePlayer);
                continue;
            }
            if (serverLevel != null) {
                @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                if (player == null) {
                    offlineGamePlayers.add(gamePlayer);
                }
            }
        }

        for (GamePlayer gamePlayer : offlineGamePlayers) {
            String playerName = gamePlayer.getPlayerName();
            if (teamManager.teamData.removePlayer(gamePlayer)) {
                BattleRoyale.LOGGER.debug("Removed offline gamePlayer {}", playerName);
            }
        }
    }

    /**
     * 防止游戏开始时有意外的无队伍GamePlayer
     */
    protected static void removeNoTeamGamePlayer() {
        TeamManager teamManager = TeamManager.get();
        List<GamePlayer> noTeamPlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamManager.teamData.getGamePlayersList()) {
            if (gamePlayer.getTeam() == null) {
                noTeamPlayers.add(gamePlayer);
            }
        }

        for (GamePlayer noTeamPlayer : noTeamPlayers) {
            if (teamManager.teamData.removePlayer(noTeamPlayer)) {
                GameMessageManager.notifyLeavedMember(noTeamPlayer.getPlayerUUID(), noTeamPlayer.getGameTeamId()); // 防止游戏开始时无队伍的GamePlayer
            }
        }
    }

    /**
     * 强制淘汰玩家，不包含发送系统消息
     * 成功淘汰后发送大厅传送消息
     */
    protected static boolean forceEliminatePlayerSilence(GamePlayer gamePlayer) {
        TeamManager teamManager = TeamManager.get();

        if (teamManager.teamData.eliminatePlayer(gamePlayer)) {
            // 强制淘汰后传送回大厅
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                if (player != null) {
                    // TODO 生成战利品盒子
                    Consumer<ServerPlayer> delayedTask = isWinner -> {
                        GameManager.get().sendLobbyTeleportMessage(player, false);
                    };
                    new DelayedEvent<>(delayedTask, player, 2, "TeamManager::GameManager.get().sendLobbyTeleportMessage");
                }
            }
            teamManager.onTeamChangedInGame();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 强制淘汰玩家并向队友发送消息
     */
    protected static void forceEliminatePlayerFromTeam(LivingEntity livingEntity) {
        TeamManager teamManager = TeamManager.get();
        @Nullable ServerPlayer player = livingEntity instanceof ServerPlayer serverPlayer ? serverPlayer : null;

        GamePlayer gamePlayer = teamManager.teamData.getGamePlayerByUUID(livingEntity.getUUID());
        if (gamePlayer == null) {
            if (player != null) {
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            }
            return;
        }

        boolean playerEliminatedBefore = gamePlayer.isEliminated();
        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        if (teamManager.teamData.eliminatePlayer(livingEntity.getUUID())) {
            if (player != null) {
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            }
            BattleRoyale.LOGGER.info("Force eliminated livingEntity {} (UUID: {}) from team {}", livingEntity.getName().getString(), livingEntity.getUUID(), gamePlayer.getGameTeamId());
        }

        GameManager gameManager = GameManager.get();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel != null) {
            if (!playerEliminatedBefore) { // 从未被淘汰到被淘汰
                gameManager.sendEliminateMessage(gamePlayer);
                ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.forced_elimination", livingEntity.getName()).withStyle(ChatFormatting.RED));
                if (gameManager.getGameEntry().forceEliminationTeleportToLobby) {
                    gameManager.teleportToLobby(livingEntity); // 不用TeamManager的teleportToLobby
                }
            } else {
                BattleRoyale.LOGGER.debug("GamePlayer {} has already been eliminated, TeamManager skipped sending chat message", gamePlayer.getPlayerName());
            }
        }
        BattleRoyale.LOGGER.info("Force removed livingEntity {} (UUID: {}) from team {}", livingEntity.getName().getString(), livingEntity.getUUID(), gamePlayer.getGameTeamId());

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            if (serverLevel != null) {
                if (!teamEliminatedBefore) {
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                } else {
                    BattleRoyale.LOGGER.debug("Team {} has already been eliminated, TeamManager skipped sending chat message", gameTeam.getGameTeamId());
                }
            }
            BattleRoyale.LOGGER.info("Team {} has been eliminated for no standing livingEntity", gameTeam.getGameTeamId());
        }
        teamManager.onTeamChangedInGame();
    }

    /**
     * 将玩家移出队伍
     * @return 是否移出队伍
     */
    protected static boolean removePlayerFromTeam(@NotNull UUID playerId) {
        TeamManager teamManager = TeamManager.get();

        GamePlayer gamePlayer = teamManager.teamData.getGamePlayerByUUID(playerId);
        if (gamePlayer == null) {
            return false;
        }
        int teamId = gamePlayer.getGameTeamId(); // 缓存teamId
        if (!teamManager.teamData.removePlayer(playerId)) {
            return false;
        }

        GameManager gameManager = GameManager.get();
        GameMessageManager.notifyLeavedMember(playerId, teamId); // 离队后通知不渲染队伍HUD
        GameMessageManager.notifyTeamChange(teamId); // 离队后通知队伍成员更新队伍HUD

        // 移除原版Team
        if (gameManager.getGameEntry().buildVanillaTeam) {
            try {
                ServerLevel serverLevel = gameManager.getServerLevel();
                if (serverLevel != null) {
                    Scoreboard scoreboard = serverLevel.getScoreboard();
                    @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(playerId) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                    if (player != null) {
                        String playerName = player.getName().getString();
                        scoreboard.removePlayerFromTeam(playerName);
                    } else {
                        BattleRoyale.LOGGER.debug("Failed to get ServerPlayer by UUID {} in TeamManagement::removePlayerFromTeam, skipped leave vanilla team", playerId);
                    }
                } else {
                    BattleRoyale.LOGGER.debug("GameManager.serverLevel is null in TeamManagement::removePlayerFromTeam, skipped leave vanilla team");
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Error in TeamManagement::removePlayerFromTeam, in remove player from vanilla team: {}", e.getMessage());
            }
        }
        return true;
    }

    /**
     * 创建并加入队伍
     * @param player 需要加入队伍的 ServerPlayer
     * @param teamId 队伍id
     * @return 是否加入队伍
     */
    protected static boolean createNewTeamAndJoin(ServerPlayer player, int teamId) {
        TeamManager teamManager = TeamManager.get();

        if (teamId < 1) {
            return false;
        }
        int newPlayerId = teamManager.teamData.generateNextPlayerId();
        if (newPlayerId < 1) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.reached_player_limit").withStyle(ChatFormatting.RED));
            return false;
        }
        String playerName = player.getName().getString();
        GameTeam newTeam = new GameTeam(teamId, teamManager.teamConfig.getTeamColor(teamId));
        if (!teamManager.teamData.addGameTeam(newTeam)) {
            BattleRoyale.LOGGER.debug("Failed to create new team {} and let {} join", teamId, playerName);
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            return false;
        }
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), playerName, newPlayerId, false, newTeam);
        if (!teamManager.teamData.addPlayerToTeam(gamePlayer, newTeam)) {
            return false;
        }

        GameManager gameManager = GameManager.get();
        GameMessageManager.notifyTeamChange(newTeam.getGameTeamId()); // 新建队伍并加入，通知更新队伍HUD
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.joined_to_team", teamId).withStyle(ChatFormatting.GREEN));

        // 加入原版Team
        if (gameManager.getGameEntry().buildVanillaTeam) {
            try {
                ServerLevel serverLevel = gameManager.getServerLevel();
                if (serverLevel != null) {
                    Scoreboard scoreboard = serverLevel.getScoreboard();
                    PlayerTeam vanillaTeam = TeamUtils.getOrCreateVanillaTeam(scoreboard, gameManager.getGameEntry().hideVanillaTeamName, newTeam);
                    scoreboard.addPlayerToTeam(playerName, vanillaTeam);
                } else {
                    BattleRoyale.LOGGER.warn("GameManager.serverLevel is null in TeamManagement::createNewTeamAndJoin, skipped build vanilla team");
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Error in TeamManagement::createNewTeamAndJoin, in build vanilla team: {}", e.getMessage());
            }
        }

        return true;
    }
}
