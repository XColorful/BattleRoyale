package xiao.battleroyale.compat.neoforge.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.network.NetworkHandler;

import java.lang.reflect.Method;

public class NeoNetworkAdapter implements INetworkAdapter {

    private final SimpleChannel channel;
    private final String protocolVersionString = String.valueOf(NetworkHandler.PROTOCOL_VERSION);

    public NeoNetworkAdapter() {
        ResourceLocation channelName = ResourceLocation.tryParse(String.format("%s:game_channel", BattleRoyale.MOD_ID));

        this.channel = NetworkRegistry.newSimpleChannel(
                channelName,
                () -> this.protocolVersionString,
                this.protocolVersionString::equals,
                this.protocolVersionString::equals
        );
    }

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction) {

        PlayNetworkDirection neoDirection = direction == MessageDirection.SERVER_TO_CLIENT
                ? PlayNetworkDirection.PLAY_TO_CLIENT
                : PlayNetworkDirection.PLAY_TO_SERVER;

        this.channel.<T>messageBuilder(clazz, id, neoDirection)
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
                PacketDistributor.ALL.noArg(),
                message
        );
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IMessage<?> message) {
        this.channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                message
        );
    }
}