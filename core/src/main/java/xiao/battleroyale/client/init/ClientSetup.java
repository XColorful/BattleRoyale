package xiao.battleroyale.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.client.screen.LootSpawnerScreen;
import xiao.battleroyale.init.registry.ModMenuTypes;
import xiao.battleroyale.inventory.LootSpawnerMenu;

import java.util.Collections;
import java.util.List;

public class ClientSetup implements IClientSetup {

    private static final ClientSetup INSTANCE = new ClientSetup();

    public static ClientSetup get() {
        return INSTANCE;
    }

    private ClientSetup() {}

    @Override
    public List<ScreenRegistration<?, ?>> getScreenRegistrations() {
        MenuType<LootSpawnerMenu> menuType = ModMenuTypes.LOOT_SPAWNER_MENU.get();
        // 用 Access Widener 改了权限
        MenuScreens.ScreenConstructor<LootSpawnerMenu, LootSpawnerScreen> factory = LootSpawnerScreen::new;

        @SuppressWarnings({"unchecked", "rawtypes"})
        ScreenRegistration<?, ?> registration = (ScreenRegistration) new ScreenRegistration(
                menuType,
                factory
        );


        return Collections.singletonList(registration);
    }
}
