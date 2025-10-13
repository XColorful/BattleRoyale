package xiao.battleroyale.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.network.NetworkHandler;

import java.util.List;

public class SendUtils {

    public static <T extends IMessage<?>> void sendMessageToPlayer(@NotNull ServerPlayer player, T message) {
        NetworkHandler.get().sendToPlayer(player, message);
    }

    public static <T extends IMessage<?>> void sendMessageToAllPlayers(T message) {
        NetworkHandler.get().sendToAllPlayers(message);
    }

    public static <T extends IMessage<?>> void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, T message, @NotNull ServerLevel serverLevel) {
        for (GamePlayer gamePlayer : gamePlayers) {
            // 只更新StandingGamePlayer，其他GamePlayer状态不会更新
            // 发送消息不依赖GameManager的运行时标记
//            if (!gamePlayer.isActiveEntity()) {
//                continue;
//            }
            @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
            if (player == null) {
                continue;
            }
            NetworkHandler.get().sendToPlayer(player, message);
        }
    }

    public static <T extends IMessage<?>> void sendMessageToTeam(GameTeam gameTeam, T message, @NotNull ServerLevel serverLevel) {
        for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
//            if (!gamePlayer.isActiveEntity()) {
//                continue;
//            }
            @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
            if (player == null) {
                continue;
            }
            NetworkHandler.get().sendToPlayer(player, message);
        }
    }
}
