package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.message.IMessage;

import java.util.function.Consumer;

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
    public void handle(ClientMessageSpectateInfo message, Consumer<Runnable> handler) {
        handler.accept(() -> {
            BattleRoyale.getClientGameDataManager().updateGameSpectateInfo(message.spectateSyncNbt);
        });
    }
}
