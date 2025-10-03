package xiao.battleroyale.compat.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

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
            ResourceLocation id
    ) {}

    private record NeoPayload<T extends IMessage<T>>(ResourceLocation id, T message) implements CustomPacketPayload {

        @Override
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

        registeredPackets.add(new RegisteredPacket<>(clazz, packetId));
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
        PacketDistributor.ALL.noArg().send(payload);
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
        PacketDistributor.PLAYER.with(player).send(payload);
    }

    @SubscribeEvent
    public void register(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(modId);

        for (RegisteredPacket<?> rp : registeredPackets) {
            registerPacketInternal(registrar, rp);
        }
    }

    private <T extends IMessage<T>> void registerPacketInternal(IPayloadRegistrar registrar, RegisteredPacket<T> rp) {

        ResourceLocation id = rp.id;
        Class<T> messageClass = rp.messageType;
        FriendlyByteBuf.Reader<NeoPayload<T>> decoder = (buf) -> NeoPayload.decode(messageClass, id, buf);
        IPayloadHandler<NeoPayload<T>> handler = (payload, context) -> {
            final T message = payload.message();
            context.workHandler().execute(() -> {
                message.handle(message, Runnable::run);
            });
        };
        registrar.common(id, decoder, handler);
    }
}