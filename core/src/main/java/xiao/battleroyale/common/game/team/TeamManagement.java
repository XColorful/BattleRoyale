package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.api.event.DelayedEvent;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TeamManagement {

    /**
     * 玩家强制加入队伍，优先加入已有队伍，其次创建新队伍
     * 适用于管理员指令或游戏初始化时的强制分配。
     * @param player 需要加入队伍的玩家
     */
    @ApiStatus.Internal
    public static void forceJoinTeam(TeamManager teamManager, LivingEntity player) {
        @Nullable ServerPlayer serverPlayer = player instanceof ServerPlayer ? (ServerPlayer) player : null;
        if (teamManager.removePlayerFromTeam(player.getUUID())) { // 加入队伍前离开当前队伍
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.leaved_current_team").withStyle(ChatFormatting.YELLOW));
        }

        int newTeamId = teamManager.findNotFullTeamId();
        if (newTeamId > 0) { // 有未满员队伍
            TeamManagement.addPlayerToTeamInternal(teamManager, player, teamManager.findNotFullTeamId(), false); // 强制加入未满员队伍(强制分配)
        } else {
            newTeamId = teamManager.teamData.generateNextTeamId();
            TeamManagement.createNewTeamAndJoin(teamManager, player, newTeamId); // 无未满员队伍则创建队伍
        }
    }

    /**
     * 指定加入的队伍，不自动将申请的玩家离开队伍
     * 不自动创建队伍，否则请使用 {}
     * @param player 需要加入队伍的玩家
     * @param targetTeamId 目标队伍的 ID
     * @param request 如果为 true，则尝试直接加入（跳过队长确认）；如果为 false，则当队伍有在线成员时发送申请。
     */
    @ApiStatus.Internal
    public static void addPlayerToTeamInternal(TeamManager teamManager, LivingEntity player, int targetTeamId, boolean request) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerPlayer serverPlayer = player instanceof ServerPlayer ? (ServerPlayer) player : null;

        UUID playerId = player.getUUID();
        GameTeam targetTeam = teamManager.teamData.getGameTeamById(targetTeamId);
        ServerLevel serverLevel = gameManager.getServerLevel();

        // 要加入的队伍不存在
        if (targetTeam == null || serverLevel == null) {
            return;
        }

        // 已经在队伍里，不自动离开队伍
        boolean isAlreadyInTeam = teamManager.teamData.getGamePlayerByUUID(playerId) != null;
        if (isAlreadyInTeam) {
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.already_in_team").withStyle(ChatFormatting.YELLOW));
            return;
        }

        // 队伍满员，无法加入
        boolean isTeamAlreadyFull = targetTeam.getTeamMembers().size() >= teamManager.teamConfig.teamSize;
        if (isTeamAlreadyFull) {
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.team_full", targetTeamId).withStyle(ChatFormatting.RED));
            return;
        }

        // 申请入队
        if (request && targetTeam.getTeamMemberCount() != 0) { // 需要申请 + 已经有人
            ServerPlayer targetPlayer = GameUtils.getServerPlayerOrNull(serverLevel, targetTeam.getLeaderUUID());
            if (targetPlayer != null) {
                if (serverPlayer != null) teamManager.requestPlayer(serverPlayer, targetPlayer);
            } else {
                BattleRoyale.LOGGER.warn("TeamManagement: team {} leader is not ServerPlayer, decline to add {} to team", targetTeam.getGameTeamId(), player.getName().getString());
            }
            return;
        }
        // 空队伍或不用申请

        // 达到人数上限
        int newPlayerId = teamManager.teamData.generateNextPlayerId();
        if (newPlayerId < 1) {
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.reached_player_limit", teamManager.teamConfig.playerLimit).withStyle(ChatFormatting.RED));
            return;
        }

        // 新建 GamePlayer
        String playerName = player.getName().getString();
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), playerName, newPlayerId, serverPlayer == null, targetTeam); // 非玩家(生物)则视为人机
        // 无法创建GamePlayer（游戏开始后会锁，内部已经封装好）
        if (!teamManager.teamData.addPlayerToTeam(gamePlayer, targetTeam)) {
            BattleRoyale.LOGGER.debug("Failed to add player {} to team {}", playerName, targetTeamId);
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.failed_to_join_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            return;
        }

        if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.joined_to_team", targetTeam.getGameTeamId()).withStyle(ChatFormatting.GREEN));
        // 通知队伍成员有新玩家加入
        TeamNotification.notifyPlayerJoinTeam(gamePlayer, serverLevel);
        // 玩家加入队伍，通知更新队伍HUD
        GameMessageManager.notifyTeamChange(targetTeam.getGameTeamId());

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
    }

    /**
     * 清理掉离线GamePlayer，防止后续影响游戏结束的人数判定
     */
    @ApiStatus.Internal
    public static void removeOfflineGamePlayer(TeamManager teamManager, ServerLevel serverLevel) {
        List<GamePlayer> offlineGamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : teamManager.teamData.getGamePlayersList()) {
            if (!gamePlayer.isActiveEntity()) {
                offlineGamePlayers.add(gamePlayer);
                continue;
            }
            if (serverLevel != null) {
                @Nullable LivingEntity player = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
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
    @ApiStatus.Internal
    public static void removeNoTeamGamePlayer(TeamManager teamManager) {
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
    @ApiStatus.Internal
    public static boolean forceEliminatePlayerSilence(TeamManager teamManager, GamePlayer gamePlayer) {
        if (teamManager.teamData.eliminatePlayer(gamePlayer)) {
            // 强制淘汰后传送回大厅
            IGameManager gameManager = BattleRoyale.getGameManager();
            ServerLevel serverLevel = gameManager.getServerLevel();
            if (serverLevel != null) {
                @Nullable LivingEntity player = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
                if (player != null) {
                    // TODO 生成战利品盒子，不一定要ServerPlayer
                    if (player instanceof ServerPlayer serverPlayer) {
                        Consumer<ServerPlayer> delayedTask = isWinner -> {
                            gameManager.getGameLobbyManager().sendLobbyTeleportMessage(serverPlayer, false);
                        };
                        new DelayedEvent<>(delayedTask, serverPlayer, 1, "TeamManager::GameManager.sendLobbyTeleportMessage");
                    }
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
    @ApiStatus.Internal
    public static void forceEliminatePlayerFromTeam(TeamManager teamManager, LivingEntity livingEntity) {
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

        IGameManager gameManager = BattleRoyale.getGameManager();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel != null) {
            if (!playerEliminatedBefore) { // 从未被淘汰到被淘汰
                gameManager.getGameProcessManager().sendEliminateMessage(serverLevel, gamePlayer);
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
    @ApiStatus.Internal
    public static boolean removePlayerFromTeam(TeamManager teamManager, @NotNull UUID playerUUID) {
        GamePlayer gamePlayer = teamManager.teamData.getGamePlayerByUUID(playerUUID);
        if (gamePlayer == null) {
            return false;
        }
        int teamId = gamePlayer.getGameTeamId(); // 缓存teamId
        if (!teamManager.teamData.removePlayer(playerUUID)) {
            return false;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        GameMessageManager.notifyLeavedMember(playerUUID, teamId); // 离队后通知不渲染队伍HUD
        GameMessageManager.notifyTeamChange(teamId); // 离队后通知队伍成员更新队伍HUD

        // 移除原版Team
        if (gameManager.getGameEntry().buildVanillaTeam) {
            try {
                ServerLevel serverLevel = gameManager.getServerLevel();
                if (serverLevel != null) {
                    Scoreboard scoreboard = serverLevel.getScoreboard();
                    @Nullable LivingEntity player = GameUtils.getLivingEntity(serverLevel, playerUUID);
                    if (player != null) {
                        String playerName = player.getName().getString();
                        scoreboard.removePlayerFromTeam(playerName);
                    } else {
                        BattleRoyale.LOGGER.debug("Failed to get ServerPlayer by UUID {} in TeamManagement::removePlayerFromTeam, skipped leave vanilla team", playerUUID);
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
     * @param player 需要加入队伍的玩家
     * @param teamId 队伍id
     * @return 是否加入队伍
     */
    @ApiStatus.Internal
    public static boolean createNewTeamAndJoin(TeamManager teamManager, LivingEntity player, int teamId) {
        @Nullable ServerPlayer serverPlayer = player instanceof ServerPlayer ? (ServerPlayer) player : null;
        if (teamId < 1) {
            return false;
        }
        int newPlayerId = teamManager.teamData.generateNextPlayerId();
        if (newPlayerId < 1) {
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.reached_player_limit").withStyle(ChatFormatting.RED));
            return false;
        }
        String playerName = player.getName().getString();
        GameTeam newTeam = new GameTeam(teamId, teamManager.teamConfig.getTeamColor(teamId));
        if (!teamManager.teamData.addGameTeam(newTeam)) {
            BattleRoyale.LOGGER.debug("Failed to create new team {} and let {} join", teamId, playerName);
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.failed_to_join_team", teamId).withStyle(ChatFormatting.RED));
            return false;
        }
        GamePlayer gamePlayer = new GamePlayer(player.getUUID(), playerName, newPlayerId, serverPlayer == null, newTeam);
        if (!teamManager.teamData.addPlayerToTeam(gamePlayer, newTeam)) {
            return false;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        GameMessageManager.notifyTeamChange(newTeam.getGameTeamId()); // 新建队伍并加入，通知更新队伍HUD
        if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.joined_to_team", teamId).withStyle(ChatFormatting.GREEN));

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
