package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xiao.battleroyale.api.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Supplier;

public class ClientMessageZoneInfo implements IMessage<ClientMessageZoneInfo> {

    private CompoundTag zoneSyncNbt;

    public ClientMessageZoneInfo() {}

    public ClientMessageZoneInfo(CompoundTag zoneSyncNbt) {
        this.zoneSyncNbt = zoneSyncNbt;
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
    public void handle(ClientMessageZoneInfo message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientGameDataManager.get().updateZoneInfo(message.zoneSyncNbt);
        });
        context.setPacketHandled(true);
    }
}