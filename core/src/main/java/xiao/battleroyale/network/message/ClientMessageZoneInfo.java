package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Consumer;

public class ClientMessageZoneInfo implements IMessage<ClientMessageZoneInfo> {

    private final @NotNull CompoundTag zoneSyncNbt;

    public ClientMessageZoneInfo() {
        zoneSyncNbt = new CompoundTag();
    }

    public ClientMessageZoneInfo(CompoundTag zoneSyncNbt) {
        this.zoneSyncNbt = zoneSyncNbt != null ? zoneSyncNbt : new CompoundTag();
    }

    @Override
    public void encode(ClientMessageZoneInfo message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.zoneSyncNbt);
    }

    public static ClientMessageZoneInfo decode(FriendlyByteBuf buffer) {
        CompoundTag receivedNbt = buffer.readNbt();
        return new ClientMessageZoneInfo(receivedNbt);
    }

    @Override
    public void handle(ClientMessageZoneInfo message, Consumer<Runnable> handler) {
        handler.accept(() -> {
            ClientGameDataManager.get().updateZoneInfo(message.zoneSyncNbt);
        });
    }
}