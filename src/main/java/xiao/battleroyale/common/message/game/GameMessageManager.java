package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.network.message.ClientMessageGameInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.List;
import java.util.function.Function;

public class GameMessageManager extends AbstractMessageManager<GameMessage> implements IGameManager {

    private static class GameMessageManagerHolder {
        private static final GameMessageManager INSTANCE = new GameMessageManager();
    }

    public static GameMessageManager get() {
        return GameMessageManagerHolder.INSTANCE;
    }

    public static final int ALIVE_CHANNEL = -1;
    public static final String ALIVE_KEY = Integer.toString(ALIVE_CHANNEL);

    @Override
    protected void checkExpiredMessage() {
        if (!GameManager.get().isInGame()) {
            super.checkExpiredMessage();
            return;
        }

        int aliveTotal = GameManager.get().getStandingGamePlayers().size();
        if (aliveTotal > 0) {
            GameMessage message = getOrCreateMessage(ALIVE_CHANNEL);
            message.standingPlayerCount = aliveTotal;
            message.nbt = message.toNBT();
            message.updateTime = currentTime;
        }
    }

    // TODO 以后增加更多游戏消息时把正的和负的进行区分或换个设计
    // 目前跟父类方法一致
    @Override
    protected CompoundTag buildCommonChangedMessage() {
        CompoundTag nbtPacket = new CompoundTag();
        for (int id : changedId) {
            GameMessage message = messages.get(id);
            if (message == null) {
                nbtPacket.put(Integer.toString(id), new CompoundTag());
            } else {
                nbtPacket.put(Integer.toString(id), message.nbt);
            }
        }
        return nbtPacket;
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
        switch (nbtId) {
            case ALIVE_CHANNEL -> {
                GameMessage message = getOrCreateMessage(nbtId);
                message.standingPlayerCount = GameManager.get().getStandingGamePlayers().size();
                message.nbt = message.toNBT();
                message.updateTime = currentTime;
                changedId.add(nbtId);
            }
            default -> {
                ;
            }
        }
    }

    @Override
    protected Function<Integer, GameMessage> createMessage() {
        return (nbtId) -> new GameMessage(new CompoundTag(), currentTime);
    }

    /**
     * IGameManager
     */

    @Override
    public void initGameConfig(ServerLevel serverLevel) {}
    @Override
    public boolean isPreparedForGame() { return true; }
    @Override
    public void initGame(ServerLevel serverLevel) {}
    @Override
    public boolean isReady() { return true; }
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        MessageManager.get().registerGameMessage();
        GameManager.get().notifyAliveChange();
        return true;
    }
    @Override
    public void onGameTick(int gameTime) {}
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        changedId.add(ALIVE_CHANNEL);
    }
}
