package xiao.battleroyale.api.init.registry;

import java.util.function.Supplier;

/**
 * 平台无关的注册对象引用接口，用于替代 Forge 的 RegistryObject。
 */
public interface IRegistryObject<T> extends Supplier<T> {

    /**
     * @return 注册的名称，如 "loot_spawner"
     */
    String getId();

    /**
     * @return 实际的注册对象
     */
    @Override
    T get();
}