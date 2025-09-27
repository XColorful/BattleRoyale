package xiao.battleroyale.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.network.message.IMessage;
import xiao.battleroyale.client.game.ClientGameDataManager;

import java.util.function.Consumer;

public class ClientMessageTeamInfo implements IMessage<ClientMessageTeamInfo> {

    private final @NotNull CompoundTag teamSyncNbt;

    public ClientMessageTeamInfo() {
        teamSyncNbt = new CompoundTag();
    }

    public ClientMessageTeamInfo(CompoundTag teamSyncNbt) {
        this.teamSyncNbt = teamSyncNbt != null ? teamSyncNbt : new CompoundTag();
    }

    @Override
    public void encode(ClientMessageTeamInfo message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.teamSyncNbt);
    }

    public static ClientMessageTeamInfo decode(FriendlyByteBuf buffer) {
        CompoundTag receivedNbt = buffer.readNbt();
        return new ClientMessageTeamInfo(receivedNbt);
    }

    @Override
    public void handle(ClientMessageTeamInfo message, Consumer<Runnable> handler) {
        handler.accept(() -> {
            ClientGameDataManager.get().updateTeamInfo(message.teamSyncNbt);
        });
    }
}
