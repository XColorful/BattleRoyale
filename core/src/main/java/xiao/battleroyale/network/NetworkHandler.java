package xiao.battleroyale.network;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.network.message.ClientMessageGameInfo;
import xiao.battleroyale.network.message.ClientMessageSpectateInfo;
import xiao.battleroyale.network.message.ClientMessageTeamInfo;
import xiao.battleroyale.network.message.ClientMessageZoneInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class NetworkHandler {

    private static NetworkHandler INSTANCE;
    private final INetworkAdapter adapter;
    private final AtomicInteger ID_COUNT = new AtomicInteger(0);
    public static final String PROTOCOL_VERSION = "1.2";
    public static boolean isProtocolAccepted(String remoteVersion) {
        return remoteVersion.equals(PROTOCOL_VERSION);
//                || remoteVersion.equals("1.2"); // 后续添加
    }
    public static Predicate<String> getProtocolAcceptancePredicate() {
        return NetworkHandler::isProtocolAccepted;
    }

    private NetworkHandler(INetworkAdapter adapter) {
        this.adapter = adapter;
    }

    public static void initialize(INetworkAdapter adapter) {
        if (INSTANCE != null) {
            throw new IllegalStateException("NetworkHandler already initialized.");
        }
        INSTANCE = new NetworkHandler(adapter);
    }

    public static NetworkHandler get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NetworkHandler not initialized. Call initialize() first.");
        }
        return INSTANCE;
    }

    public void registerMessages() {
        MessageDirection direction = MessageDirection.SERVER_TO_CLIENT;

        // 服务端 -> 客户端消息
        adapter.registerMessage(ID_COUNT.getAndIncrement(), ClientMessageZoneInfo.class, direction);
        adapter.registerMessage(ID_COUNT.getAndIncrement(), ClientMessageTeamInfo.class, direction);
        adapter.registerMessage(ID_COUNT.getAndIncrement(), ClientMessageGameInfo.class, direction);
        adapter.registerMessage(ID_COUNT.getAndIncrement(), ClientMessageSpectateInfo.class, direction);

        // 客户端 -> 服务端消息
        // 暂无
    }

    public void sendToAllPlayers(IMessage<?> message) {
        adapter.sendToAll(message);
    }

    public void sendToPlayer(@NotNull ServerPlayer player, IMessage<?> message) {
        adapter.sendToPlayer(player, message);
    }
}