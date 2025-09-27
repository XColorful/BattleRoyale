package xiao.battleroyale.compat.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.IRegistrar;
import xiao.battleroyale.api.init.IRegistryObject;

import java.util.function.Supplier;

/**
 * IRegistrar 的 Forge 实现，包装了 Forge 的 DeferredRegister。
 */
public class ForgeRegistrar<T> implements IRegistrar<T> {
    private final DeferredRegister<T> deferredRegister;

    public ForgeRegistrar(DeferredRegister<T> deferredRegister) {
        this.deferredRegister = deferredRegister;
    }

    @Override
    public <V extends T> IRegistryObject<V> register(String name, Supplier<? extends V> supplier) {
        RegistryObject<V> registryObject = deferredRegister.register(name, supplier);
        BattleRoyale.LOGGER.debug("Registering Forge item: {}", name);
        return new ForgeRegistryObject<>(registryObject, name);
    }

    @Override
    public void registerAll(Object registrarHook) {
        if (registrarHook instanceof IEventBus eventBus) {
            deferredRegister.register(eventBus);
        } else {
            BattleRoyale.LOGGER.error("Invalid registrar hook provided for ForgeRegistrar: {}", registrarHook.getClass().getName());
        }
    }
}
