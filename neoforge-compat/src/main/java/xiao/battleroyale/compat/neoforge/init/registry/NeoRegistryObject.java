package xiao.battleroyale.compat.neoforge.init.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import xiao.battleroyale.api.init.registry.IRegistryObject;

public class NeoRegistryObject<R, T extends R> implements IRegistryObject<T> {

    private final DeferredHolder<R, T> registryObject;
    private final String id;

    public NeoRegistryObject(DeferredHolder<R, T> deferredHolder, String id) {
        this.registryObject = deferredHolder;
        this.id = id;
    }

    @Override
    public String getId() {
        return registryObject.getId().getPath();
    }

    @Override
    public T get() {
        return registryObject.get();
    }
}