package xiao.battleroyale.compat.neoforge.init.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.registry.IRegistrar;
import xiao.battleroyale.api.init.registry.IRegistryObject;

import java.util.function.Supplier;

/**
 * IRegistrar 的 NeoForge 实现，包装了 NeoForge 的 DeferredRegister。
 */
public class NeoRegistrar<T> implements IRegistrar<T> {
    private final DeferredRegister<T> deferredRegister;

    public NeoRegistrar(DeferredRegister<T> deferredRegister) {
        this.deferredRegister = deferredRegister;
    }

    @Override
    public <V extends T> IRegistryObject<V> register(String name, Supplier<? extends V> supplier) {
        DeferredHolder<T, V> deferredHolder = deferredRegister.register(name, supplier);
        BattleRoyale.LOGGER.debug("Registering NeoForge item: {}", name);
        return new NeoRegistryObject<>(deferredHolder, name);
    }

    @Override
    public void registerAll(Object registrarHook) {
        if (registrarHook instanceof IEventBus eventBus) {
            deferredRegister.register(eventBus);
        } else {
            BattleRoyale.LOGGER.error("Invalid registrar hook provided for NeoRegistrar: {}", registrarHook.getClass().getName());
        }
    }
}