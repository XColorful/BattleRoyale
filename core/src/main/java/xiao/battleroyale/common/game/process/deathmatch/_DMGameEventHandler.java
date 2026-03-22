package xiao.battleroyale.common.game.process.deathmatch;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.List;

public class _DMGameEventHandler {

    /**
     * 由 BRGameProcessManager 衍生而来 {@link xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager#onPlayerDown}
     */
    protected static boolean onPlayerDown(DMGameProcessManager dmGameProcessManager, ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, boolean removeInvalidTeam) {
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
            gameManager.onPlayerDeath(event, gamePlayer);
            return false;
        }

        LivingEntity player = event.getEntity();
        PlayerRevive playerRevive = PlayerRevive.get();

        // PlayerRevive倒地机制：取消事件并设置为流血状态
        if (playerRevive.isBleeding(player)) {
            gamePlayer.setAlive(false);
            playerRevive.addBleedingPlayer(player);
            dmGameProcessManager.sendDownMessage(gameManager.getServerLevel(), gamePlayer);
            return true;
        }

        if (!gamePlayer.isAlive()) { // 倒地，但是不为存活状态
            BattleRoyale.LOGGER.debug("GamePlayer {} is down but not alive, switch to onPlayerDeath", gamePlayer.getPlayerName());
            gameManager.onPlayerDeath(event, gamePlayer);
            return false;
        }

        // 没检测到 PlayerRevive 就认为是其他手段自救
        gamePlayer.setAlive(true); // 其实应该不需要设置
        BattleRoyale.LOGGER.debug("Not detected GamePlayer {} PlayerRevive, may be revived by any method", gamePlayer.getNameWithId());
        return true;
    }

    /**
     * 由 BRGameProcessManager 衍生而来 {@link xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager#onPlayerDeath}
     *
     * 统计数据在 IStatsManager 监听 onGamePlayerDeath 记录 {@link xiao.battleroyale.common.game.stats.StatsManager#onRecordPlayerDeath}
     * IGameProcessManager 执行后紧接着就是 GamePlayerDeathFinishEvent {@link xiao.battleroyale.common.game.GameManager#onPlayerDeath}
     */
    protected static boolean onPlayerDeath(DMGameProcessManager dmGameProcessManager, @Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        boolean isDeathByAttacker = false;

        // --------正常 onPlayerDeath 逻辑 --------
        // 即 BRGameProcessManager#onPlayerDeath 开头

        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        boolean playerEliminatedBefore = gamePlayer.isEliminated();
        if (teamEliminatedBefore && playerEliminatedBefore) {
            BattleRoyale.LOGGER.debug("GamePlayer {} and GameTeam {} already eliminated, skipped onPlayerDeath", gamePlayer.getPlayerName(), gamePlayer.getTeam().getGameTeamId());
            return false;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        PlayerRevive playerRevive = PlayerRevive.get();


        // 死亡事件本身已经跳过非 standingPlayer
        // 单独淘汰，连带淘汰放在后面进行
        if (!playerEliminatedBefore) { // 第一次淘汰才尝试kill，避免重复kill
            gamePlayer.setEliminated(true); // GamePlayer 内部会自动让 GameTeam 更新 eliminated
            if (dmGameProcessManager.addAndTrackRestandingGamePlayer(gamePlayer)) { // 任意方式死亡都要重生
                isDeathByAttacker |= contributeDeathMatchKill(dmGameProcessManager, gameManager, event, serverLevel, gamePlayer);
            }
            // gameManager.getTeamManager().forceEliminatePlayerSilence(gamePlayer); // 不需要 TeamManager 级别的本局内永久 eliminate
            // dmGameProcessManager.sendEliminateMessage(serverLevel, gamePlayer); // 不需要通知 eliminate，有效的 eliminate 已提前发消息

            // 最后再 kill，此时再触发 onPlayerDeath 不会被 TeamManager 级别 eliminated 拦截
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
                if (dmGameProcessManager.addAndTrackRestandingGamePlayer(member)) { // 任意方式死亡都要重生
                    isDeathByAttacker |= contributeDeathMatchKill(dmGameProcessManager, gameManager, event, serverLevel, gamePlayer);
                }
                // dmGameProcessManager.sendEliminateMessage(serverLevel, member); // 不需要通知 eliminate，有效的 eliminate 已提前发消息

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

        if (isDeathByAttacker) { // 对淘汰数有贡献
            gameManager.addFinishCheckAfterDeathEvent();
        } else {
            BattleRoyale.LOGGER.debug("onPlayerDeath (GamePlayer {}) not detected attacker GamePlayer, skipped addFinishCheckAfterDeathEvent", gamePlayer.getNameWithId());
        }
        return true; // 触发 Finish 事件 (记录 Stats)，但不参与死斗模式胜利判定
    }

    private static boolean contributeDeathMatchKill(DMGameProcessManager dmGameProcessManager, IGameManager gameManager, @Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        // --------DeathMatch 特殊记分逻辑--------

        // 只记录玩家击杀数，非玩家击杀的不算 (不已死亡数为胜利条件)
        @Nullable Entity attackerEntity = event != null ? event.getSource().getEntity() : null;
        @Nullable GamePlayer attackerGamePlayer = attackerEntity != null ? gameManager.getTeamManager().getGamePlayerByUUID(attackerEntity.getUUID()) : null;
        boolean isDeathByAttacker = attackerGamePlayer != null;
        // 仅在被玩家击杀时通知，区别于 BattleRoyale 模式 (任意方式死亡都算淘汰)
        if (isDeathByAttacker) {
            dmGameProcessManager.sendEliminateMessage(serverLevel, gamePlayer);
            dmGameProcessManager.addGamePlayerKill(attackerGamePlayer, 1); // 给攻击者的队伍加分数
            BattleRoyale.LOGGER.debug("onPlayerDeath (GamePlayer {}) detected attacker GamePlayer {}", gamePlayer.getNameWithId(), attackerGamePlayer.getNameWithId());
        }

        return isDeathByAttacker;
    }
}
