package xiao.battleroyale.common.game.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.util.ChatUtils;

/**
 * 该类仅用于抽离TeamManager的功能实现，简化TeamManager
 * 类似.h和.cpp的设计
 */
public class TeamNotification {

    /**
     * 通知队伍原玩家新成员入队
     * @param newGamePlayer 新入队的成员
     */
    public static void notifyPlayerJoinTeam(GamePlayer newGamePlayer, ServerLevel serverLevel) {
        GameTeam gameTeam = newGamePlayer.getTeam();
        if (serverLevel == null) {
            return;
        }
        String newPlayerName = newGamePlayer.getPlayerName();
        for (GamePlayer member : gameTeam.getTeamMembers()) {
            if (member == newGamePlayer) {
                continue;
            }
            @Nullable ServerPlayer teamPlayer = serverLevel.getPlayerByUUID(member.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
            if (teamPlayer != null) {
                ChatUtils.sendComponentMessageToPlayer(teamPlayer, Component.translatable("battleroyale.message.player_joined_team", newPlayerName).withStyle(ChatFormatting.GREEN));
            }
        }
    }

    public static void sendPlayerTeamId(ServerPlayer player) {
        TeamManager teamManager = TeamManager.get();
        GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.not_in_a_team").withStyle(ChatFormatting.RED));
            return;
        }
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.your_team_id", gamePlayer.getGameTeamId()).withStyle(ChatFormatting.AQUA));
    }
}
