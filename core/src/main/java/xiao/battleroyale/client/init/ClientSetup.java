package xiao.battleroyale.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.client.gui.LootSpawnerScreen;
import xiao.battleroyale.init.registry.ModMenuTypes;

public class ClientSetup implements IClientSetup {

    private static final ClientSetup INSTANCE = new ClientSetup();

    public static ClientSetup get() {
        return INSTANCE;
    }

    private ClientSetup() {}

    @Override
    public void onClientSetup() {
        MenuScreens.register(ModMenuTypes.LOOT_SPAWNER_MENU.get(), LootSpawnerScreen::new);
    }
}
