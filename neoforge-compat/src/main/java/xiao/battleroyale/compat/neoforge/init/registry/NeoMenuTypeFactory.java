package xiao.battleroyale.compat.neoforge.init.registry;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import xiao.battleroyale.api.init.registry.IMenuTypeFactory;

/**
 * IMenuTypeFactory 的 NeoForge 实现，使用 MenuType 的构造函数来适配网络菜单创建。
 */
public class NeoMenuTypeFactory implements IMenuTypeFactory {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockEntityMenu(IMenuTypeCreationHandler<T> factory) {
        IContainerFactory<T> neoForgeFactory = factory::create;
        return IMenuTypeExtension.create(neoForgeFactory);
    }
}