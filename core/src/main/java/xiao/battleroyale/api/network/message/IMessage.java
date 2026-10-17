package xiao.battleroyale.api.network.message;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

/**
 * T 必须保持无界：改成 {@code T extends IMessage<T>} 会把方法擦除签名从 Object 改为 IMessage，
 * 使旧扩展 mod 的桥方法不再覆盖本接口，导致消息收发被静默跳过
 */
public interface IMessage<T> {

    /**
     * 过渡期旧签名，供已发布的扩展 mod 继续实现；BattleRoyale 本体只实现无参重载
     */
    @Deprecated(forRemoval = true)
    default void encode(T message, FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException(
                getClass().getName() + " does not implement encode(FriendlyByteBuf)");
    }

    @SuppressWarnings("unchecked")
    default void encode(FriendlyByteBuf buffer) {
        encode((T) this, buffer);
    }

    /**
     * 过渡期旧签名，供已发布的扩展 mod 继续实现；BattleRoyale 本体只实现无参重载
     */
    @Deprecated(forRemoval = true)
    default void handle(T message, Consumer<Runnable> handler) {
        throw new UnsupportedOperationException(
                getClass().getName() + " does not implement handle(Consumer)");
    }

    @SuppressWarnings("unchecked")
    default void handle(Consumer<Runnable> handler) {
        handle((T) this, handler);
    }
}
