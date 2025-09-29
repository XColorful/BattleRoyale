package xiao.battleroyale.api.network.message;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

public interface IMessage<T> {

    void encode(T message, FriendlyByteBuf buffer);

    void handle(T message, Consumer<Runnable> handler);
}
