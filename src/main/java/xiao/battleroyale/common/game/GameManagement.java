package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类仅用于抽离GameManager的功能实现，简化GameManager
 * 类似.h和.cpp的设计
 */
public class GameManagement {

    /**
     * 检查所有传入的游戏玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    protected static void checkAndUpdateInvalidGamePlayer(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        GameManager gameManager = GameManager.get();
        List<GamePlayer> invalidPlayers = new ArrayList<>();
        // 筛选并增加无效时间计数
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isBot()) { // 真人玩家
                updateInvalidServerPlayer(gamePlayer, serverLevel, invalidPlayers, gameManager.getGameEntry().maxPlayerInvalidTime);
            } else { // 人机
                updateInvalidBotPlayer(gamePlayer, serverLevel, invalidPlayers, gameManager.getGameEntry().maxBotInvalidTime);
            }
        }

        // 清理无效玩家
        if (!invalidPlayers.isEmpty()) {
            for (GamePlayer invalidPlayer : invalidPlayers) {
                if (TeamManager.get().forceEliminatePlayerSilence(invalidPlayer)) { // 强制淘汰了玩家，不一定都在此处淘汰
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", invalidPlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
                    BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {})", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
                }
            }
        }
    }
    public static void updateInvalidServerPlayer(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers, int maxInvalidTime) {
        ServerPlayer serverPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (serverPlayer == null) { // 不在线或者不在游戏运行的 serverLevel
            if (gamePlayer.isActiveEntity()) {
                GameNotification.notifyGamePlayerIsInactive(serverLevel, gamePlayer);
            }
            gamePlayer.setActiveEntity(false);
            gamePlayer.addInvalidTime();
            if (GameManager.get().eliminateInactiveTeam(gamePlayer)) { // 队伍全员离线
                return;
            } else if (gamePlayer.getInvalidTime() >= maxInvalidTime) { // 达到允许的最大离线时间
                invalidPlayers.add(gamePlayer); // 淘汰单个离线玩家
            }
        } else { // 更新最后有效位置
            if (!gamePlayer.isActiveEntity()) { // 刚上线
                GameNotification.notifyGamePlayerIsActive(serverLevel, gamePlayer);
                float lastHealth = gamePlayer.getLastHealth();
                if (lastHealth <= 0) {
                    invalidPlayers.add(gamePlayer);
                    return;
                }
                // TODO GamePlayer health 和 absorptionAmount 处理
                serverPlayer.setHealth(lastHealth); // 不用maxHealth检查，可能包含吸收血量
            }
            gamePlayer.setActiveEntity(true);
            gamePlayer.setLastHealth(serverPlayer.getHealth());
            gamePlayer.setLastPos(serverPlayer.position());
        }
    }
    public static void updateInvalidBotPlayer(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers, int maxInvalidTime) {
        Entity entity = serverLevel.getEntity(gamePlayer.getPlayerUUID());
        if (!(entity instanceof LivingEntity livingEntity)) {
            if (gamePlayer.isActiveEntity()) {
                GameNotification.notifyGamePlayerIsInactive(serverLevel, gamePlayer);
            }
            gamePlayer.setActiveEntity(false);
            gamePlayer.addInvalidTime();
            if (GameManager.get().eliminateInactiveTeam(gamePlayer)) { // 队伍全员离线啊
                return;
            } else if (gamePlayer.getInvalidTime() >= maxInvalidTime) {
                invalidPlayers.add(gamePlayer); // 淘汰单个人机
            }
        } else {
            if (!gamePlayer.isActiveEntity()) { // 刚上线
                GameNotification.notifyGamePlayerIsActive(serverLevel, gamePlayer);
                float lastHealth = gamePlayer.getLastHealth();
                if (lastHealth <= 0) {
                    invalidPlayers.add(gamePlayer);
                }
                livingEntity.setHealth(lastHealth);
            }
            gamePlayer.setActiveEntity(true);
            gamePlayer.setLastHealth(livingEntity.getHealth());
            gamePlayer.setLastPos(livingEntity.position());
        }
    }

    /**
     * 检查是否只有倒地或不在线玩家，逐个淘汰
     */
    protected static boolean eliminateInactiveTeam(@Nullable ServerLevel serverLevel, GamePlayer invalidPlayer) {
        GameTeam gameTeam = invalidPlayer.getTeam();
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (teamMember.isActiveEntity() || teamMember.isAlive()) { // 有在线的未倒地玩家
                return false;
            }
        }
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (TeamManager.get().forceEliminatePlayerSilence(teamMember)) {
                if (serverLevel != null) {
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", teamMember.getPlayerName()).withStyle(ChatFormatting.GRAY));
                }
                BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {}) for inactive team", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
        return true;
    }
}
