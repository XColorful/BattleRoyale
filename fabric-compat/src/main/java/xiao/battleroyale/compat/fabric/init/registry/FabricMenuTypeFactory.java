package xiao.battleroyale.compat.fabric.init.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import xiao.battleroyale.api.init.registry.IMenuTypeFactory;

public class FabricMenuTypeFactory implements IMenuTypeFactory {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockEntityMenu(IMenuTypeCreationHandler<T> factory) {
        // 使用 Fabric API 创建支持额外数据的菜单类型
        return new ExtendedScreenHandlerType<>(factory::create);
    }
}