package xiao.battleroyale.compat.forge.init.registry;

import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.api.init.registry.IRegistryObject;

/**
 * IRegistryObject 的 Forge 实现，包装了 Forge 的 RegistryObject。
 */
public class ForgeRegistryObject<T> implements IRegistryObject<T> {
    private final RegistryObject<T> registryObject;
    private final String id;

    public ForgeRegistryObject(RegistryObject<T> registryObject, String id) {
        this.registryObject = registryObject;
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
