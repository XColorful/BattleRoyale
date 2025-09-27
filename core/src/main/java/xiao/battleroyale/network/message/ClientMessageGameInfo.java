package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Consumer;

public class ClientMessageGameInfo implements IMessage<ClientMessageGameInfo> {

    private final @NotNull CompoundTag gameSyncNbt;

    public ClientMessageGameInfo(CompoundTag gameSyncNbt) {
        this.gameSyncNbt = gameSyncNbt != null ? gameSyncNbt : new CompoundTag();
    }

    @Override
    public void encode(ClientMessageGameInfo message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.gameSyncNbt);
    }

    public static ClientMessageGameInfo decode(FriendlyByteBuf buffer) {
        CompoundTag receivedNbt = buffer.readNbt();
        return new ClientMessageGameInfo(receivedNbt);
    }

    @Override
    public void handle(ClientMessageGameInfo message, Consumer<Runnable> handler) {
        handler.accept(() -> {
            ClientGameDataManager.get().updateGameInfo(message.gameSyncNbt);
        });
    }
}
