package xiao.battleroyale.compat.neoforge.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.client.init.ClientSetup;
import xiao.battleroyale.client.renderer.CustomRenderType;

public class NeoClientSetup {

    private static final IClientSetup CLIENT_SETUP = ClientSetup.get();
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CustomRenderType._registerRenderPipelines();
            
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        for (IClientSetup.ScreenRegistration<?, ?> registration : CLIENT_SETUP.getScreenRegistrations()) {
            try {
                event.register(
                        (MenuType) registration.menuType(),
                        (MenuScreens.ScreenConstructor) registration.factory()
                );
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to register screen for menu type: {}", registration.menuType().toString(), e);
            }
        }
    }
}