package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.network.message.ClientMessageGameInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.List;
import java.util.function.Function;

public class GameInfoMessageManager extends AbstractMessageManager<GameInfoMessage> implements IGameManager {

    private static class GameMessageManagerHolder {
        private static final GameInfoMessageManager INSTANCE = new GameInfoMessageManager();
    }

    public static GameInfoMessageManager get() {
        return GameMessageManagerHolder.INSTANCE;
    }

    public static final int ALIVE_CHANNEL = -1;
    public static final String ALIVE_KEY = Integer.toString(ALIVE_CHANNEL);
    public static final int GAMEID_CHANNEL = -2;
    public static final String GAMEID_KEY = Integer.toString(GAMEID_CHANNEL);

    @Override
    protected void checkExpiredMessage() {
        updateAliveTotal();

        if (!GameManager.get().isInGame()) {
            super.checkExpiredMessage();
        }
    }

    protected void updateAliveTotal() {
        GameManager gameManager = GameManager.get();
        boolean inGame = gameManager.isInGame();
        if (inGame) {
            int aliveTotal = GameTeamManager.getStandingGamePlayers().size();
            GameInfoMessage message = getOrCreateMessage(ALIVE_CHANNEL);
            message.updateMessage(aliveTotal, gameManager.getGameId());
            message.nbt = message.toNBT();
            message.updateTime = currentTime;
        } else {
            if (messages.containsKey(ALIVE_CHANNEL)) {
                GameInfoMessage message = getOrCreateMessage(ALIVE_CHANNEL);
                message.updateMessage(0, gameManager.getGameId());
                message.nbt = new CompoundTag();
                message.updateTime = currentTime;
            } else {
                ;
            }
        }
        changedId.add(ALIVE_CHANNEL);
    }

    // TODO 以后增加更多游戏消息时把正的和负的进行区分或换个设计
    @Override
    protected CompoundTag buildCommonChangedMessage() {
        return super.buildCommonChangedMessage();
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
                updateAliveTotal();
            }
            default -> {
                ;
            }
        }
    }

    @Override
    protected Function<Integer, GameInfoMessage> createMessage() {
        return (nbtId) -> new GameInfoMessage(new CompoundTag(), currentTime);
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
        updateAliveTotal();
        return true;
    }
    @Override
    public void onGameTick(int gameTime) {}
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        updateAliveTotal();
        sendMessages(); // 暂时没有什么好的解决方案，至少Bug修了
        clear();
    }
}
