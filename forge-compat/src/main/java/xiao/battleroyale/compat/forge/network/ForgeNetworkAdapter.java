package xiao.battleroyale.compat.forge.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.network.NetworkHandler;

import java.util.Optional;

public class ForgeNetworkAdapter implements INetworkAdapter {

    private final SimpleChannel channel;

    public ForgeNetworkAdapter() {
        this.channel = NetworkRegistry.newSimpleChannel(
                BattleRoyale.getMcRegistry().createResourceLocation(String.format("%s:game_channel", BattleRoyale.MOD_ID)),
                () -> NetworkHandler.PROTOCOL_VERSION,
                NetworkHandler.getProtocolAcceptancePredicate(), // 服务端 -> 客户端
                NetworkHandler.getProtocolAcceptancePredicate() // 客户端 -> 服务端
        );
    }

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction) {
        NetworkDirection forgeDirection = direction == MessageDirection.SERVER_TO_CLIENT
                ? NetworkDirection.PLAY_TO_CLIENT
                : NetworkDirection.PLAY_TO_SERVER;

        this.channel.registerMessage(
                id,
                clazz,
                (messageInstance, buffer) -> messageInstance.encode(messageInstance, buffer),
                (buffer) -> {
                    try {
                        return (T) clazz.getDeclaredMethod("decode", net.minecraft.network.FriendlyByteBuf.class).invoke(null, buffer);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to decode message " + clazz.getName(), e);
                    }
                },
                (message, contextSupplier) -> {

                    message.handle(message, (work) -> {
                        contextSupplier.get().enqueueWork(work);
                    });

                    contextSupplier.get().setPacketHandled(true);
                },
                Optional.of(forgeDirection)
        );
    }

    @Override
    public void sendToAll(IMessage<?> message) {
        this.channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IMessage<?> message) {
        this.channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                message
        );
    }
}