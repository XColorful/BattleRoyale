package xiao.battleroyale.compat.forge;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import xiao.battleroyale.api.init.IMenuTypeFactory;

/**
 * IMenuTypeFactory 的 Forge 实现，使用 IForgeMenuType.create() 来实现网络传输的菜单创建。
 */
public class ForgeMenuTypeFactory implements IMenuTypeFactory {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockEntityMenu(IMenuTypeCreationHandler<T> factory) {
        return IForgeMenuType.create(factory::create);
    }
}