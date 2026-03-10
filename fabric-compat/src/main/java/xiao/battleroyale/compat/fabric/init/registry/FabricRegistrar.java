package xiao.battleroyale.compat.fabric.init.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;

import java.util.function.Supplier;

/**
 * IRegistrar 的 Fabric 实现，直接使用 Vanilla Registry。
 */
public class FabricRegistrar<T> implements IRegistrar<T> {
    private final Registry<T> registry;
    private final String modId;

    public FabricRegistrar(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    @Override
    public <V extends T> IRegistryObject<V> register(String name, Supplier<? extends V> supplier) {
        V value = supplier.get();
        // Fabric 环境下直接向原版注册表注册
        Registry.register(registry, new ResourceLocation(modId, name), value);
        BattleRoyale.LOGGER.debug("Registering Fabric object: {}:{}", modId, name);
        return new FabricRegistryObject<>(value, name);
    }

    @Override
    public void registerAll(Object registrarHook) {
        // Fabric 是即时注册的，这里通常为空
        // 如果在 registerAll 时才触发逻辑，可以在这里处理
    }
}