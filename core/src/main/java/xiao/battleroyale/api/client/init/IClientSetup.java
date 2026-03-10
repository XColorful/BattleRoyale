package xiao.battleroyale.api.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.List;

public interface IClientSetup {

    List<ScreenRegistration<?, ?>> getScreenRegistrations();

    // 用 Access Widener 改了权限
    record ScreenRegistration<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>>(
            MenuType<T> menuType,
            MenuScreens.ScreenConstructor<T, U> factory
    ) {}
}
