package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Supplier;

public class ClientMessageSpectateInfo implements IMessage<ClientMessageSpectateInfo> {

    private final @NotNull CompoundTag spectateSyncNbt;

    public ClientMessageSpectateInfo(CompoundTag spectateSyncNbt) {
        this.spectateSyncNbt = spectateSyncNbt != null ? spectateSyncNbt : new CompoundTag();
    }

    @Override
    public void encode(ClientMessageSpectateInfo message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.spectateSyncNbt);
    }

    public static ClientMessageSpectateInfo decode(FriendlyByteBuf buffer) {
        CompoundTag receivedNbt = buffer.readNbt();
        return new ClientMessageSpectateInfo(receivedNbt);
    }

    @Override
    public void handle(ClientMessageSpectateInfo message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientGameDataManager.get().updateGameSpectateInfo(message.spectateSyncNbt);
        });
        context.setPacketHandled(true);
    }
}
