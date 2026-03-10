package xiao.battleroyale.compat.fabric.init.registry;

import xiao.battleroyale.api.init.registry.IRegistryObject;

/**
 * IRegistryObject 的 Fabric 实现，直接包装对象实例。
 */
public class FabricRegistryObject<T> implements IRegistryObject<T> {
    private final T value;
    private final String id;

    public FabricRegistryObject(T value, String id) {
        this.value = value;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public T get() {
        return value;
    }
}