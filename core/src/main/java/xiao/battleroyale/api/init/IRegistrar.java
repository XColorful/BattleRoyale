package xiao.battleroyale.api.init;

import java.util.function.Supplier;

/**
 * 平台无关的注册对象集合接口。
 * 核心模块通过此接口定义内容，兼容层负责实际的注册实现。
 */
public interface IRegistrar<T> {

    /**
     * 注册一个新的对象到集合中。
     * @param name 对象的注册名
     * @param supplier 对象的构造函数
     * @return 平台无关的注册对象引用
     */
    <V extends T> IRegistryObject<V> register(final String name, final Supplier<? extends V> supplier);

    /**
     * 平台兼容层必须实现此方法以执行实际的注册操作（如注册到 Forge 的 IEventBus）。
     * (在 Forge 兼容层中，此方法会调用 DeferredRegister.register(bus))
     * @param registrarHook 实际的注册钩子，例如 Forge 的 IEventBus
     */
    void registerAll(Object registrarHook);
}
