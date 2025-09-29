package xiao.battleroyale.api.network;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.network.message.IMessage;

public interface INetworkAdapter {

    /**
     * 注册一个消息类型。
     * 必须在 Mod 加载阶段（CommonSetup 或更早）调用。
     * @param id 消息ID。
     * @param clazz 消息的类。
     * @param direction 消息的方向（客户端->服务器 或 服务器->客户端）。
     */
    <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction);

    void sendToAll(IMessage<?> message);

    void sendToPlayer(ServerPlayer player, IMessage<?> message);
}