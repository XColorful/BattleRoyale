package xiao.battleroyale.api.init.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * 抽象菜单类型创建工厂，用于封装 Forge/NeoForge/Fabric 的网络菜单创建逻辑。
 */
public interface IMenuTypeFactory {

    /**
     * 创建一个 MenuType，该 MenuType 能够从网络数据包中重建 AbstractContainerMenu。
     */
    <T extends AbstractContainerMenu> MenuType<T> createBlockEntityMenu(IMenuTypeCreationHandler<T> factory);

    @FunctionalInterface
    interface IMenuTypeCreationHandler<T extends AbstractContainerMenu> {
        /**
         * 处理菜单创建逻辑：从网络数据包中读取 BlockPos 等信息并创建菜单实例。
         */
        T create(int windowId, Inventory inv, FriendlyByteBuf buf);
    }
}