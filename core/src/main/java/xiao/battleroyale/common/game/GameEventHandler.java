package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.util.ChatUtils;

/**
 * 该类仅用于抽离GameManager的功能实现，简化GameManager
 */
public class GameEventHandler {

    protected static void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate) {
        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            if (serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) != null) { // 不一定在大逃杀游戏的维度
                gamePlayer.setActiveEntity(true);
            }
            if (GameManager.get().isInGame() && gamePlayer.isEliminated()) {
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
                GameManager.get().teleportToLobby(player); // 淘汰的传送回大厅，防止干扰游戏
            }
            return;
        }

        if (GameManager.get().isInGame()) {
            GameNotification.sendGameSpectateMessage(player, !onlyGamePlayerSpectate); // 提供游戏信息及观战指令
        } else { // 没开游戏就加入
            if (TeamManager.get().shouldAutoJoin()) {
                TeamManager.get().joinTeam(player);
                GameManager.get().teleportToLobby(player); // 登录自动传到大厅
            }
        }
    }

    protected static void onPlayerLoggedOut(boolean isInGame, ServerPlayer player) {
        if (!isInGame) {
            if (TeamManager.get().leaveTeam(player)) { // 没开始游戏就等于离队
                BattleRoyale.LOGGER.debug("Player {} logged out, leave GamePlayer", player.getName().getString());
            }
        }

        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            GameManager.get().finishGameIfShouldEnd(); // 玩家登出服务器时的防御检查
        }
    }

    /**
     * 检查GamePlayer是被不死图腾救了还是PlayerRevive倒地
     * 没有队友时不允许倒地直接让PlayerRevive击杀掉
     * PlayerRevive只允许玩家倒地，因此人机玩家无法倒地
     */
    protected static void onPlayerDown(@NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam) {
        // 不允许倒地的情况：队友没有Alive的
        GameTeam gameTeam = gamePlayer.getTeam();
        boolean hasAliveMember = false;
        for (GamePlayer member : gameTeam.getAlivePlayers()) { // 直接忽略被淘汰的队友
            if (member.getGameSingleId() == gamePlayer.getGameSingleId()) {
                continue;
            }
            if (removeInvalidTeam && !member.isActiveEntity()) { // 队友离线算作倒地 && 队友离线
                continue;
            }
            hasAliveMember = true;
            break;
        }
        if (!hasAliveMember) { // 没有存活队友就判定为无法救援，直接判死亡
            BattleRoyale.LOGGER.debug("GamePlayer {} is down and has no alive member", gamePlayer.getPlayerName());
            onPlayerDeath(GameManager.get().getServerLevel(), gamePlayer);
            return;
        }

        // PlayerRevive倒地机制：取消事件并设置为流血状态
        if (livingEntity instanceof Player player) {
            PlayerRevive playerRevive = PlayerRevive.get();
            if (playerRevive.isBleeding(player)) {
                gamePlayer.setAlive(false);
                playerRevive.addBleedingPlayer(player);
                GameManager.get().sendDownMessage(gamePlayer);
                return;
            }
        }

        if (!gamePlayer.isAlive()) { // 倒地，但是不为存活状态
            BattleRoyale.LOGGER.debug("GamePlayer {} is down but not alive, switch to onPlayerDeath", gamePlayer.getPlayerName());
            onPlayerDeath(GameManager.get().getServerLevel(), gamePlayer);
            GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
        }

        // 没检测到PlayerRevive就认为是不死图腾救了
        // 实际貌似不会触发log，不清楚不死图腾原理
        // 只能认为不死图腾的功能不是自救，而是阻止倒地
        gamePlayer.setAlive(true); // 其实应该不需要设置
        BattleRoyale.LOGGER.debug("Not detected PlayerRevive, should be revived by Totem of Undying");
    }

    protected static void onPlayerDeath(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        boolean playerEliminatedBefore = gamePlayer.isEliminated();
        gamePlayer.setEliminated(true); // GamePlayer内部会自动让GameTeam更新eliminated
        TeamManager.get().forceEliminatePlayerSilence(gamePlayer); // 提醒 TeamManager 内部更新 standingPlayer信息
        // 死亡事件会跳过非standingPlayer，放心kill
        if (!playerEliminatedBefore) { // 第一次淘汰才尝试kill，淘汰后被打倒的不管
            GameManager.get().sendEliminateMessage(gamePlayer);
            PlayerRevive playerRevive = PlayerRevive.get();
            if (serverLevel != null) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player != null && playerRevive.isBleeding(player)) {
                    BattleRoyale.LOGGER.debug("Detected GamePlayer {} PlayerRevive.isBleeding, force kill", gamePlayer.getPlayerName());
                    playerRevive.kill(player);
                }
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in onPlayerDeath, skipped PlayerRevive check");
            }
        }

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            // 队伍淘汰则倒地队友全部kill
            BattleRoyale.LOGGER.info("Team {} has been eliminated, updating member to eliminated", gameTeam.getGameTeamId());
            for (GamePlayer member : gameTeam.getTeamMembers()) {
                if (!member.isEliminated()) {
                    onPlayerDeath(serverLevel, member);
                }
            }
            if (serverLevel != null) {
                if (!teamEliminatedBefore) {
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                } else {
                    BattleRoyale.LOGGER.debug("Team {} has already been eliminated, GameManager skipped sending chat message", gameTeam.getGameTeamId());
                }
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in onPlayerDeath, skipped sending chat message");
            }
            GameManager.get().finishGameIfShouldEnd(); // 游戏队伍被淘汰时的检查
        }
        GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
        GameMessageManager.notifyAliveChange();
    }

    protected static void onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        if (!GameTeamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID()) || gamePlayer.isEliminated()) { // 该GamePlayer已经不是未被淘汰玩家
            BattleRoyale.LOGGER.debug("GamePlayer {} is not a standing game player, skipped revive", gamePlayer.getPlayerName());
            return;
        }
        gamePlayer.setAlive(true);
        GameManager.get().sendReviveMessage(gamePlayer);
        BattleRoyale.LOGGER.info("GamePlayer {} has revived, singleId:{}", gamePlayer.getPlayerName(), gamePlayer.getGameSingleId());
    }
}
