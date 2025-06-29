package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Supplier;

public class ClientMessageGameInfo implements IMessage<ClientMessageGameInfo> {

    private final @NotNull CompoundTag gameZoneNbt;

    public ClientMessageGameInfo(CompoundTag gameZoneNbt) {
        this.gameZoneNbt = gameZoneNbt != null ? gameZoneNbt : new CompoundTag();
    }

    @Override
    public void encode(ClientMessageGameInfo message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.gameZoneNbt);
    }

    public static ClientMessageGameInfo decode(FriendlyByteBuf buffer) {
        CompoundTag receivedNbt = buffer.readNbt();
        return new ClientMessageGameInfo(receivedNbt);
    }

    @Override
    public void handle(ClientMessageGameInfo message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientGameDataManager.get().updateGameInfo(message.gameZoneNbt);
        });
        context.setPacketHandled(true);
    }
}
