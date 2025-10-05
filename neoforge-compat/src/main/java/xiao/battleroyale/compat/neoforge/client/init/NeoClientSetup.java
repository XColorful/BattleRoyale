package xiao.battleroyale.compat.neoforge.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.api.client.init.ScreenRegistration;
import xiao.battleroyale.client.init.ClientSetup;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class NeoClientSetup {

    private static final IClientSetup CLIENT_SETUP = ClientSetup.get();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (ScreenRegistration<?, ?> registration : CLIENT_SETUP.getScreenRegistrations()) {
                try {
                    MenuScreens.register(
                            (MenuType) registration.menuType(),
                            (MenuScreens.ScreenConstructor) registration.factory()
                    );
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Failed to register screen for menu type: {}", registration.menuType().toString(), e);
                }
            }
        });
    }
}