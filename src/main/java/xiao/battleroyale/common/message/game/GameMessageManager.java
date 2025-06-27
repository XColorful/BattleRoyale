package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.network.message.ClientMessageGameInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.List;
import java.util.function.Function;

public class GameMessageManager extends AbstractMessageManager<GameMessage> {

    private static class GameMessageManagerHolder {
        private static final GameMessageManager INSTANCE = new GameMessageManager();
    }

    public static GameMessageManager get() {
        return GameMessageManagerHolder.INSTANCE;
    }

    public static int ALIVE_CHANNEL = -1;
    public static String ALIVE_KEY = Integer.toString(ALIVE_CHANNEL);

    @Override
    protected void checkExpiredMessage() {
        // 特殊字段强制保活
        int aliveTotal = GameManager.get().getStandingGamePlayers().size();
        if (aliveTotal > 0) {
            GameMessage message = getOrCreateMessage(ALIVE_CHANNEL);
            message.standingPlayerCount = aliveTotal;
            message.nbt = message.toNBT();
            message.updateTime = currentTime;
        }

        // 待更新
        // super.checkExpiredMessage();
    }

    @Override
    protected void sendMessageToPlayers(List<ServerPlayer> players, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageGameInfo message = new ClientMessageGameInfo(nbtMessage);
        for (ServerPlayer player : players) {
            SendUtils.sendMessageToPlayer(player, message);
        }
    }

    @Override
    protected void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageGameInfo message = new ClientMessageGameInfo(nbtMessage);
        SendUtils.sendMessageToGamePlayers(gamePlayers, message, serverLevel);
    }

    @Override
    public void notifyNbtChange(int nbtId) {
        GameMessage message = getOrCreateMessage(nbtId);
        message.standingPlayerCount = GameManager.get().getStandingGamePlayers().size();
        message.nbt = message.toNBT();
        message.updateTime = currentTime;
        changedId.add(nbtId);
    }

    @Override
    protected Function<Integer, GameMessage> createMessage() {
        return (nbtId) -> new GameMessage(new CompoundTag(), currentTime);
    }
}
