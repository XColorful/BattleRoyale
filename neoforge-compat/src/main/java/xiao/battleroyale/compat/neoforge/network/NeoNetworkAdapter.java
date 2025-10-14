package xiao.battleroyale.compat.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.api.network.message.IMessage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NeoNetworkAdapter implements INetworkAdapter {

    private record RegisteredPacket<T extends IMessage<T>>(
            Class<T> messageType,
            ResourceLocation id,
            MessageDirection direction
    ) {}

    private record NeoPayload<T extends IMessage<T>>(ResourceLocation id, T message) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return new Type<>(this.id);
        }

        public void write(FriendlyByteBuf buffer) {
            this.message.encode(this.message, buffer);
        }

        @SuppressWarnings("unchecked")
        public static <T extends IMessage<T>> NeoPayload<T> decode(Class<T> clazz, ResourceLocation id, FriendlyByteBuf buffer) {
            try {
                Method decodeMethod = clazz.getDeclaredMethod("decode", FriendlyByteBuf.class);
                T message = (T) decodeMethod.invoke(null, buffer);
                return new NeoPayload<>(id, message);
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to decode message {} for ID {}", clazz.getName(), id, e);
                throw new RuntimeException("Failed to decode message " + clazz.getName(), e);
            }
        }
    }

    private final List<RegisteredPacket<?>> registeredPackets = new ArrayList<>();
    private final String modId = BattleRoyale.MOD_ID;

    public NeoNetworkAdapter() {
    }

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction) {
        String path = clazz.getSimpleName().toLowerCase();
        ResourceLocation packetId = ResourceLocation.tryParse(String.format("%s:%s", modId, path));

        if (packetId == null) {
            BattleRoyale.LOGGER.error("Failed to create ResourceLocation for message class: {}", clazz.getName());
            return;
        }

        registeredPackets.add(new RegisteredPacket<>(clazz, packetId, direction));
    }

    @Override
    public void sendToAll(IMessage<?> message) {
        this.registeredPackets.stream()
                .filter(rp -> rp.messageType().isInstance(message))
                .findFirst()
                .ifPresent(packetInfo -> {
                    this.sendInternal((RegisteredPacket) packetInfo, message);
                });
    }
    private <T extends IMessage<T>> void sendInternal(RegisteredPacket<T> packetInfo, IMessage<?> message) {
        @SuppressWarnings("unchecked")
        T castedMessage = (T) message;

        CustomPacketPayload payload = new NeoPayload<>(packetInfo.id(), castedMessage);
        PacketDistributor.sendToAllPlayers(payload);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IMessage<?> message) {
        this.registeredPackets.stream()
                .filter(rp -> rp.messageType().isInstance(message))
                .findFirst()
                .ifPresent(packetInfo -> {
                    this.sendToPlayerInternal(player, (RegisteredPacket) packetInfo, message);
                });
    }
    private <T extends IMessage<T>> void sendToPlayerInternal(ServerPlayer player, RegisteredPacket<T> packetInfo, IMessage<?> message) {
        @SuppressWarnings("unchecked")
        T castedMessage = (T) message;

        CustomPacketPayload payload = new NeoPayload<>(packetInfo.id(), castedMessage);
        PacketDistributor.sendToPlayer(player, payload);
    }

    @SubscribeEvent
    public void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(modId);

        for (RegisteredPacket<?> rp : registeredPackets) {
            registerPacketInternal(registrar, rp);
        }
    }

    private <T extends IMessage<T>> void registerPacketInternal(PayloadRegistrar registrar, RegisteredPacket<T> rp) {
        ResourceLocation id = rp.id;
        Class<T> messageClass = rp.messageType;

        CustomPacketPayload.Type<NeoPayload<T>> payloadType = new CustomPacketPayload.Type<>(id);

        StreamCodec<FriendlyByteBuf, NeoPayload<T>> codec = StreamCodec.of(
                (buf, payload) -> payload.write(buf),
                (buf) -> NeoPayload.decode(messageClass, id, buf)
        );

        IPayloadHandler<NeoPayload<T>> handler = (payload, context) -> {
            final T message = payload.message();
            context.enqueueWork(() -> {
                message.handle(message, Runnable::run);
            });
        };

        if (rp.direction == MessageDirection.SERVER_TO_CLIENT) {
            // 客户端接收并处理消息 (Server to Client)
            registrar.playToClient(payloadType, codec, handler);
        } else if (rp.direction == MessageDirection.CLIENT_TO_SERVER) {
            // 服务器接收并处理消息 (Client to Server)
            registrar.playToServer(payloadType, codec, handler);
        } else {
            // 默认或双向消息
            registrar.playBidirectional(payloadType, codec, handler);
        }
    }
}