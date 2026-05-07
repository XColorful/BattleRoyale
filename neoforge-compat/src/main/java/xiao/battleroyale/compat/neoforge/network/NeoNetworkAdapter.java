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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NeoNetworkAdapter implements INetworkAdapter {

    private record RegisteredPacket<T extends IMessage<T>>(
            Class<T> messageType,
            ResourceLocation id,
            Function<FriendlyByteBuf, T> decoder
    ) {}

    private record NeoPayload<T extends IMessage<T>>(ResourceLocation id, T message) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return new Type<>(this.id);
        }

        public void write(FriendlyByteBuf buffer) {
            this.message.encode(this.message, buffer);
        }
    }

    private final List<RegisteredPacket<?>> registeredPackets = new ArrayList<>();
    private final String modId = BattleRoyale.MOD_ID;

    public NeoNetworkAdapter() {
    }

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, Function<FriendlyByteBuf, T> decoder, MessageDirection direction) {
        String path = clazz.getSimpleName().toLowerCase();
        ResourceLocation packetId = ResourceLocation.tryParse(String.format("%s:%s", modId, path));

        if (packetId == null) {
            BattleRoyale.LOGGER.error("Failed to create ResourceLocation for message class: {}", clazz.getName());
            return;
        }

        registeredPackets.add(new RegisteredPacket<>(clazz, packetId, decoder));
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

        CustomPacketPayload.Type<NeoPayload<T>> payloadType = new CustomPacketPayload.Type<>(id);

        StreamCodec<FriendlyByteBuf, NeoPayload<T>> codec = StreamCodec.of(
                (buf, payload) -> payload.write(buf),
                (buffer) -> new NeoPayload<>(id, rp.decoder.apply(buffer))
        );

        IPayloadHandler<NeoPayload<T>> handler = (payload, context) -> {
            final T message = payload.message();
            context.enqueueWork(() -> {
                message.handle(message, Runnable::run);
            });
        };

        registrar.playBidirectional(payloadType, codec, handler);
    }
}