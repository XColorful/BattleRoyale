package xiao.battleroyale.api.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage<T> {

    void encode(T message, FriendlyByteBuf buffer);

    void handle(T messagae, Supplier<NetworkEvent.Context> supplier);
}
