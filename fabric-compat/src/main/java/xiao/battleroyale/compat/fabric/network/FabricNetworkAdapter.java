package xiao.battleroyale.compat.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.MessageDirection;
import xiao.battleroyale.api.network.message.IMessage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Fabric 网络适配器实现
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FabricNetworkAdapter implements INetworkAdapter {

    private static final ResourceLocation CHANNEL = new ResourceLocation(String.format("%s:game_channel", BattleRoyale.MOD_ID));

    private final Map<Integer, Class> idToClass = new HashMap<>();
    private final Map<Class, Integer> classToId = new HashMap<>();

    private boolean serverReceiverRegistered = false;
    private boolean clientReceiverRegistered = false;

    @Override
    public <T extends IMessage<T>> void registerMessage(int id, Class<T> clazz, MessageDirection direction) {
        idToClass.put(id, clazz);
        classToId.put(clazz, id);

        if (direction == MessageDirection.CLIENT_TO_SERVER) {
            if (!serverReceiverRegistered) {
                registerServerGlobalReceiver();
                serverReceiverRegistered = true;
            }
        } else {
            if (!clientReceiverRegistered && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientReceiver.register(CHANNEL, this);
                clientReceiverRegistered = true;
            }
        }
    }

    private void registerServerGlobalReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            Class clazz = idToClass.get(id);
            if (clazz != null) {
                IMessage message = decode(clazz, buf);
                server.execute(() -> message.handle(message, task -> ((Runnable) task).run()));
            }
        });
    }

    @Override
    public void sendToAll(IMessage<?> message) {
        Object gameInstance = FabricLoader.getInstance().getGameInstance();
        if (gameInstance instanceof MinecraftServer server) {
            FriendlyByteBuf buf = createEncodedBuf(message);
            for (ServerPlayer player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, CHANNEL, buf);
            }
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IMessage<?> message) {
        ServerPlayNetworking.send(player, CHANNEL, createEncodedBuf(message));
    }

    private FriendlyByteBuf createEncodedBuf(IMessage<?> message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        Integer id = classToId.get(message.getClass());

        if (id == null) {
            throw new RuntimeException("Message type not registered: " + message.getClass());
        }

        buf.writeInt(id);
        ((IMessage) message).encode(message, buf);
        return buf;
    }

    private IMessage decode(Class clazz, FriendlyByteBuf buf) {
        try {
            Method method = clazz.getDeclaredMethod("decode", FriendlyByteBuf.class);
            return (IMessage) method.invoke(null, buf);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode network message: " + clazz.getName(), e);
        }
    }

    private static class ClientReceiver {
        private static void register(ResourceLocation channel, FabricNetworkAdapter adapter) {
            ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler, buf, responseSender) -> {
                int id = buf.readInt();
                Class clazz = adapter.idToClass.get(id);
                if (clazz != null) {
                    IMessage message = adapter.decode(clazz, buf);
                    client.execute(() -> message.handle(message, task -> ((Runnable) task).run()));
                }
            });
        }
    }
}