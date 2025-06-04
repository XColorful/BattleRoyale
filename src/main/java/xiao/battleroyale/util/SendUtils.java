package xiao.battleroyale.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.IMessage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.network.GameInfoHandler;

import java.util.List;

public class SendUtils {

    public static <T extends IMessage<?>> void sendMessageToPlayer(@NotNull ServerPlayer player, T message) {
        GameInfoHandler.sendToPlayer(player, message);
    }

    public static <T extends IMessage<?>> void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, T message, @NotNull ServerLevel serverLevel) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isActiveEntity()) {
                continue;
            }
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }
            GameInfoHandler.sendToPlayer(player, message);
        }
    }

    public static <T extends IMessage<?>> void sendMessageToTeam(GameTeam gameTeam, T message, @NotNull ServerLevel serverLevel) {
        for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
            if (!gamePlayer.isActiveEntity()) {
                continue;
            }
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }
            GameInfoHandler.sendToPlayer(player, message);
        }
    }
}
