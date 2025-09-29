package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.network.message.ClientMessageSpectateInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SpectateMessageManager extends AbstractMessageManager<SpectateMessage> {

    private static class SpectateMessageManagerHolder {
        private static final SpectateMessageManager INSTANCE = new SpectateMessageManager();
    }

    public static SpectateMessageManager get() {
        return SpectateMessageManagerHolder.INSTANCE;
    }

    public static final int SPECTATE_CHANNEL = -1;
    public static final String SPECTATE_KEY = Integer.toString(SPECTATE_CHANNEL);

    private static int sendPlayerPerMessage = 20; // 防止极端情况大量发送
    public static void setSendPlayerPerMessage(int playerTotal) { sendPlayerPerMessage = playerTotal; }
    private static int sendInterval = 1;
    public static void setSendInterval(int interval) { sendInterval = interval; }

    @Override
    protected void sendMessageToPlayers(List<ServerPlayer> players, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageSpectateInfo message = new ClientMessageSpectateInfo(nbtMessage);
        for (ServerPlayer player : players) {
            SendUtils.sendMessageToPlayer(player, message);
        }
    }

    @Override
    protected void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageSpectateInfo message = new ClientMessageSpectateInfo(nbtMessage);
        SendUtils.sendMessageToGamePlayers(gamePlayers, message, serverLevel);
    }

    @Override
    protected void checkExpiredMessage() {
    }

    @Override
    public void notifyNbtChange(int nbtId) {
        changedId.add(nbtId);
        if (!messages.containsKey(nbtId)) { // 初次创建时才传入完整GamePlayers
            messages.put(nbtId, new SpectateMessage(new CompoundTag(), currentTime, GameTeamManager.getGamePlayers()));
        }
    }

    @Override
    protected CompoundTag buildCommonChangedMessage() {
        return new CompoundTag();
    }

    @Override
    protected void sendMessages() {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        List<GamePlayer> sendGamePlayer = new ArrayList<>(); // 只装一个
        List<Integer> unfinishedId = new ArrayList<>();

        for (int id : changedId) {
            SpectateMessage message = getOrCreateMessage(id);
            message.updateTime = currentTime;
            // 发送冷却
            if (--message.sendCooldown > 0) {
                continue;
            }
            message.sendCooldown = sendInterval;

            // 构建消息
            message.nbt = message.toNBT(sendPlayerPerMessage);
            if (!message.nbt.isEmpty()) {
                sendGamePlayer.clear();
                sendGamePlayer.add(GameTeamManager.getGamePlayerBySingleId(id));
                if (serverLevel != null) {
                    sendMessageToGamePlayers(sendGamePlayer, message.nbt, serverLevel);
                }
                // 消息还没发完
                if (!message.isFinished()) {
                    unfinishedId.add(id);
                    continue;
                }
            }
            // 消息发完就删了
            messages.remove(id);
        }
        // 循环外统一添加
        changedId.clear();
        changedId.addAll(unfinishedId);
    }

    @Override
    protected Function<Integer, SpectateMessage> createMessage() {
        return (nbtId) -> new SpectateMessage(new CompoundTag(), currentTime, new ArrayList<>());
    }
}
