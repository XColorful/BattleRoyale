package xiao.battleroyale.compat.fabric.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.client.init.ClientSetup;

public class FabricClientSetup {
    private static final IClientSetup CLIENT_SETUP = ClientSetup.get();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {
        for (IClientSetup.ScreenRegistration<?, ?> registration : CLIENT_SETUP.getScreenRegistrations()) {
            // Fabric 直接调用原版注册方法
            MenuScreens.register(
                    (MenuType) registration.menuType(),
                    (MenuScreens.ScreenConstructor) registration.factory()
            );
        }
    }
}