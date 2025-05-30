package xiao.battleroyale.common.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.network.GameInfoHandler;
import xiao.battleroyale.network.message.ClientMessageZoneInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SyncData extends AbstractGameManagerData{

    private static final String DATA_NAME = "SyncData";

    private final Map<Integer, Pair<CompoundTag, Integer>> zoneInfo = new ConcurrentHashMap<>();
    private final Set<Integer> changedZoneId = ConcurrentHashMap.newKeySet();

    private int lastExpireTime = 0;
    private final int KEEP_TIME = 3 * 20; // 保存3秒
    private final int EXPIRE_FREQUENCY = 5 * 20; // 5秒进行一次清理

    public SyncData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        zoneInfo.clear();
        changedZoneId.clear();
    }

    @Override
    public void startGame() {
        clear();
        lastExpireTime = -EXPIRE_FREQUENCY;
    }

    @Override
    public void endGame() {
        changedZoneId.addAll(zoneInfo.keySet());
        syncInfo(Integer.MAX_VALUE);
        clear();
    }

    /**
     * 由 GameManager 调用并传入 gameTime
     * @param gameTime 当前游戏时间
     */
    public void syncInfo(int gameTime) {
        // 清理过期信息
        if (gameTime - lastExpireTime >= EXPIRE_FREQUENCY) {
            cleanExpiredZoneInfo(gameTime);
            lastExpireTime = gameTime;
        }

        // 同步信息
        this.syncZoneInfo(gameTime);
    }

    private void syncZoneInfo(int gameTime) {
        if (changedZoneId.isEmpty()) {
            return;
        }

        CompoundTag syncPacketNbt = new CompoundTag();
        for (int id : changedZoneId) {
            Pair<CompoundTag, Integer> data = zoneInfo.get(id);
            if (data != null) {
                syncPacketNbt.put(String.valueOf(id), data.first);
            } else {
                syncPacketNbt.put(String.valueOf(id), new CompoundTag());
            }
        }

        // 发送消息给所有客户端
        GameInfoHandler.sendToAllPlayers(new ClientMessageZoneInfo(syncPacketNbt));

        changedZoneId.clear();
    }

    /**
     * 添加需要同步的 Zone 信息，传入null视为清理
     * @param id 覆盖的id，理应同一个圈用一个
     * @param newInfo 发送到客户端的 NBT
     */
    public void addZoneInfo(int id, @Nullable CompoundTag newInfo) {
        if (newInfo == null) {
            deleteZoneInfo(id);
            return;
        }

        zoneInfo.put(id, Pair.of(newInfo, GameManager.get().getGameTime() + KEEP_TIME));
        changedZoneId.add(id);
    }

    private void deleteZoneInfo(int id) {
        if (zoneInfo.remove(id) != null) {
            changedZoneId.add(id);
        }
    }

    private void cleanExpiredZoneInfo(int gameTime) {
        zoneInfo.entrySet().removeIf(entry -> {
            if (entry.getValue().second < gameTime) {
                changedZoneId.add(entry.getKey());
                return true;
            }
            return false;
        });
    }

    public record Pair<A, B>(A first, B second) {

        public static <A, B> Pair<A, B> of(A first, B second) {
                return new Pair<>(first, second);
            }
        }
}