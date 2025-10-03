package xiao.battleroyale.compat.forge.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.network.NetworkHandler;

import java.lang.reflect.Method;

public class ForgeNetworkAdapter implements INetworkAdapter {

    private final SimpleChannel channel;

    public ForgeNetworkAdapter() {
        int protocolVersion = NetworkHandler.PROTOCOL_VERSION;
        Channel.VersionTest acceptedVersions = Channel.VersionTest.exact(protocolVersion);
        this.channel = ChannelBuilder
                .named(BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:game_channel", BattleRoyale.MOD_ID)))
                .networkProtocolVersion(protocolVersion) // 协议版本必须是 int
                .clientAcceptedVersions(acceptedVersions) // 客户端接受版本
                .serverAcceptedVersions(acceptedVersions) // 服务端接受版本
                .simpleChannel(); // 创建 SimpleChannel 实例
    }

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction) {
        NetworkDirection forgeDirection = direction == MessageDirection.SERVER_TO_CLIENT
                ? NetworkDirection.PLAY_TO_CLIENT
                : NetworkDirection.PLAY_TO_SERVER;

        this.channel.<T>messageBuilder(clazz, id, forgeDirection)
                .encoder((messageInstance, buffer) -> messageInstance.encode(messageInstance, buffer))
                .decoder((buffer) -> {
                    try {
                        Method decodeMethod = clazz.getDeclaredMethod("decode", net.minecraft.network.FriendlyByteBuf.class);
                        return (T) decodeMethod.invoke(null, buffer);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to decode message " + clazz.getName(), e);
                    }
                })
                .consumerMainThread((message, context) -> {
                    message.handle(message, context::enqueueWork);
                })
                .add();
    }

    @Override
    public void sendToAll(IMessage<?> message) {
        this.channel.send(
                message,
                PacketDistributor.ALL.noArg()
        );
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IMessage<?> message) {
        this.channel.send(
                message,
                PacketDistributor.PLAYER.with(player)
        );
    }
}