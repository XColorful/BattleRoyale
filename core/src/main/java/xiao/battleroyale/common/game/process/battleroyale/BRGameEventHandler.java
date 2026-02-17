package xiao.battleroyale.common.game.process.battleroyale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.List;

public class BRGameEventHandler {

    protected static void onPlayerLoggedIn(BRGameProcessManager brGameProcessManager, @NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate) {
        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            if (GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID()) != null) { // 不一定在大逃杀游戏的维度
                gamePlayer.setActiveEntity(true);
            }
            IGameManager gameManager = BattleRoyale.getGameManager();
            if (gameManager.isInGame() && gamePlayer.isEliminated()) {
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
                gameManager.teleportToLobby(player); // 淘汰的传送回大厅，防止干扰游戏
            }
            return;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) {
            gameManager.getGameProcessManager().sendGameSpectateMessage(player, !onlyGamePlayerSpectate); // 提供游戏信息及观战指令
        } else { // 没开游戏就加入
            if (teamManager.shouldAutoJoin()) {
                teamManager.joinTeam(player);
                gameManager.teleportToLobby(player); // 登录自动传到大厅
            }
        }
    }

    protected static void onPlayerLoggedOut(BRGameProcessManager brGameProcessManager, boolean isInGame, ServerPlayer player) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        ITeamManager teamManager = gameManager.getTeamManager();
        if (!isInGame) {
            if (teamManager.leaveTeam(player)) { // 没开始游戏就等于离队
                BattleRoyale.LOGGER.debug("Player {} logged out, leave GamePlayer", player.getName().getString());
            }
        }

        GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            brGameProcessManager.finishGameIfShouldEnd(gameManager); // 玩家登出服务器时的防御检查
        }
    }

    /**
     * 非游戏玩家免伤机制在 {@link xiao.battleroyale.common.game.gamerule.AttackEventHandler} 提前处理
     * (对于大逃杀) 这里实际上什么也不需要做
     * 命中伤害显示等也是 StatsManager 的事情
     */
    protected static void onPlayerDamage(BRGameProcessManager brGameProcessManager, ILivingDamageEvent event, @NotNull GamePlayer gamePlayer) {
    }

    /**
     * 检查GamePlayer是被任意手段救了还是PlayerRevive倒地
     * 没有队友时不允许倒地直接让PlayerRevive击杀掉
     * PlayerRevive只允许玩家倒地，因此人机玩家无法倒地
     */
    protected static void onPlayerDown(BRGameProcessManager brGameProcessManager, ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, boolean removeInvalidTeam) {
        IGameManager gameManager = BattleRoyale.getGameManager();

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

        // 没有存活队友就判定为无法救援，直接判死亡
        if (!hasAliveMember) {
            BattleRoyale.LOGGER.debug("GamePlayer {} is down and has no alive member, switch to onPlayerDeath", gamePlayer.getPlayerName());
            gameManager.onPlayerDeath(event, gamePlayer); // onPlayerDeath 里会在本次 onPlayerDownFinish 前设置好 eliminated
            return;
        }

        LivingEntity player = event.getEntity();
        PlayerRevive playerRevive = PlayerRevive.get();

        // PlayerRevive倒地机制：取消事件并设置为流血状态
        if (playerRevive.isBleeding(player)) {
            gamePlayer.setAlive(false);
            playerRevive.addBleedingPlayer(player);
            brGameProcessManager.sendDownMessage(gameManager.getServerLevel(), gamePlayer);
            return;
        }

        if (!gamePlayer.isAlive()) { // 倒地，但是不为存活状态
            BattleRoyale.LOGGER.debug("GamePlayer {} is down but not alive, switch to onPlayerDeath", gamePlayer.getPlayerName());
            gameManager.onPlayerDeath(event, gamePlayer);
            return;
        }

        // 没检测到 PlayerRevive 就认为是其他手段自救
        gamePlayer.setAlive(true); // 其实应该不需要设置
        BattleRoyale.LOGGER.debug("Not detected GamePlayer {} PlayerRevive, may be revived by any method", gamePlayer.getNameWithId());
    }

    protected static void onPlayerDeath(BRGameProcessManager brGameProcessManager, @Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        boolean playerEliminatedBefore = gamePlayer.isEliminated();
        if (teamEliminatedBefore && playerEliminatedBefore) {
            BattleRoyale.LOGGER.debug("GamePlayer {} and GameTeam {} already eliminated, skipped onPlayerDeath", gamePlayer.getPlayerName(), gamePlayer.getTeam().getGameTeamId());
            return;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        PlayerRevive playerRevive = PlayerRevive.get();

        // 死亡事件本身已经跳过非 standingPlayer
        // 单独淘汰，连带淘汰放在后面进行
        if (!playerEliminatedBefore) { // 第一次淘汰才尝试kill，避免重复kill
            gamePlayer.setEliminated(true); // GamePlayer 内部会自动让 GameTeam 更新 eliminated
            gameManager.getTeamManager().forceEliminatePlayerSilence(gamePlayer); // 提醒 TeamManager 内部更新 standingPlayer 信息
            brGameProcessManager.sendEliminateMessage(serverLevel, gamePlayer);

            // 最后再 kill，此时再触发 onPlayerDeath 已提前被 eliminated 拦截
            @Nullable LivingEntity player = serverLevel != null ? GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID()) : null;
            if (player != null && playerRevive.isBleeding(player)) {
                BattleRoyale.LOGGER.debug("Detected GamePlayer {} PlayerRevive.isBleeding, force kill", gamePlayer.getPlayerName());
                playerRevive.kill(player);
            }

            GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            GameMessageManager.notifyAliveChange();
        }

        // 连带淘汰在同一个 onPlayerDeath 里处理，连带触发的都在开头标志位提前拦截
        GameTeam gameTeam = gamePlayer.getTeam();
        if (!teamEliminatedBefore && gameTeam.isTeamEliminated()) {
            BattleRoyale.LOGGER.info("Team {} has been eliminated, updating member to eliminated", gameTeam.getGameTeamId());
            List<GamePlayer> nonEliminatedMember = gameTeam.getTeamMembers().stream().filter(member -> !member.isEliminated()).toList();

            // 队伍淘汰则倒地队友全部 kill
            nonEliminatedMember.forEach(member -> member.setEliminated(true)); // 提前设置 eliminate 以跳过下一次 kill 触发的 onPlayerDeath 开头检查
            for (GamePlayer member : nonEliminatedMember) {
                brGameProcessManager.sendEliminateMessage(serverLevel, member);

                // 有倒地状态就让 PlayerRevive 的 kill 后自动处理 onPlayerDeath
                @Nullable LivingEntity player = serverLevel != null ? GameUtils.getLivingEntity(serverLevel, member.getPlayerUUID()) : null;
                if (player != null && playerRevive.isBleeding(player)) {
                    playerRevive.kill(player);
                } else { // 否则手动通知 onPlayerDeath
                    gameManager.onPlayerDeath(null, member);
                }
            }

            // 发送队伍淘汰消息
            if (serverLevel != null) {
                ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            } else {
                BattleRoyale.LOGGER.error("GameManager.serverLevel is null in onPlayerDeath, skipped sending chat message");
            }
            GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            GameMessageManager.notifyAliveChange();
        }

        brGameProcessManager.finishGameIfShouldEnd(gameManager);
    }

    protected static void onPlayerRevived(BRGameProcessManager brGameProcessManager, @NotNull GamePlayer gamePlayer) {
        if (!GameTeamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID()) || gamePlayer.isEliminated()) { // 该GamePlayer已经不是未被淘汰玩家
            BattleRoyale.LOGGER.debug("GamePlayer {} is not a standing game player, skipped revive", gamePlayer.getPlayerName());
            return;
        }
        gamePlayer.setAlive(true);
        brGameProcessManager.sendReviveMessage(BattleRoyale.getGameManager().getServerLevel(), gamePlayer);
        BattleRoyale.LOGGER.info("GamePlayer {} has revived, singleId:{}", gamePlayer.getPlayerName(), gamePlayer.getGameSingleId());
    }
}
