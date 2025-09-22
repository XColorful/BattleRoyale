package xiao.battleroyale.common.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.message.IMessageManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractMessageManager<K extends AbstractCommonMessage> implements IMessageManager {

    protected int currentTime = 0;

    protected final Map<Integer, K> messages = new HashMap<>();
    protected final Set<Integer> changedId = new HashSet<>();
    protected int lastCleanTime = 0;
    protected static int CLEAN_FREQUENCY = 20 * 7;
    public static void setCleanFrequency(int freq) { CLEAN_FREQUENCY = Math.max(freq, 0); }
    protected static int EXPIRE_TIME = 20 * 5;
    public static void setExpireTime(int time) { EXPIRE_TIME = Math.max(Math.max(time, CLEAN_FREQUENCY), 0); } // 过期时间不小于清理频率
    protected static int FORCE_SYNC_FREQUENCY = 20 * 5;
    public static void setForceSyncFrequency(int freq) { FORCE_SYNC_FREQUENCY = Math.max(Math.max(freq, EXPIRE_TIME), 0); } // 强制同步频率小于过期时间

    public List<Integer> getMessagesIdList() { return messages.keySet().stream().toList(); }
    public int getCurrentTime() { return currentTime; }
    public int messagesSize() { return messages.size(); }
    public int changedIdSize() { return changedId.size(); }
    public int getLastCleanTime() { return lastCleanTime; }
    public int cleanFrequency() { return CLEAN_FREQUENCY; }
    public int expireTime() { return EXPIRE_TIME; }
    public int forceSyncFrequency() { return FORCE_SYNC_FREQUENCY; }

    @Override
    public void register(int registerTime) {
        currentTime = registerTime;
    }

    @Override
    public void unregister() {
        clear();
    }

    @Override
    public void tickMessage() {
        currentTime++;
        if (currentTime - lastCleanTime >= CLEAN_FREQUENCY) {
            checkExpiredMessage();
            lastCleanTime = currentTime;
        }

        if (currentTime % FORCE_SYNC_FREQUENCY == 0) {
            changedId.addAll(messages.keySet());
        } else if (changedId.isEmpty()) {
            return;
        }

        sendMessages();
    }

    protected void checkExpiredMessage() {
        messages.entrySet().removeIf(entry -> {
            K message = entry.getValue();
            if (currentTime - message.updateTime > EXPIRE_TIME) {
                changedId.add(entry.getKey());
                return true;
            }
            return false;
        });
    }

    protected void sendMessages() {
        CompoundTag nbtPacket = buildCommonChangedMessage();

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }
        sendMessageToGamePlayers(GameTeamManager.getGamePlayers(), nbtPacket, serverLevel);
        changedId.clear();
    }

    protected CompoundTag buildCommonChangedMessage() {
        CompoundTag nbtPacket = new CompoundTag();
        for (int id : changedId) {
            K message = messages.get(id);
            nbtPacket.put(Integer.toString(id), message != null ? message.nbt : new CompoundTag());
        }
        return nbtPacket;
    }

    @Override
    public void addNbtMessage(int nbtId, @Nullable CompoundTag nbtMessage) {
        if (nbtMessage == null || nbtMessage.isEmpty()) {
            messages.remove(nbtId);
        } else {
            K message = getOrCreateMessage(nbtId);
            message.nbt = nbtMessage;
            message.updateTime = currentTime;
        }
        changedId.add(nbtId);
    }

    @Override
    public void extendMessageTime(int nbtId, int extendTime) {
        K message = messages.get(nbtId);
        if (message == null) {
            return;
        }
        message.updateTime += extendTime;
    }

    protected K getOrCreateMessage(int nbtId) {
        return messages.computeIfAbsent(nbtId, key -> createMessage().apply(key));
    }

    @Nullable
    public K getMessage(int nbtId) {
        return messages.get(nbtId);
    }

    protected abstract Function<Integer, K> createMessage();

    @Override
    public boolean messageFinished() {
        return messages.isEmpty() && changedId.isEmpty();
    }

    protected void clear() {
        changedId.addAll(messages.keySet());
        messages.clear();
        sendMessages();
        changedId.clear();
    }

    protected abstract void sendMessageToPlayers(List<ServerPlayer> players, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel);
    protected abstract void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel);
}
