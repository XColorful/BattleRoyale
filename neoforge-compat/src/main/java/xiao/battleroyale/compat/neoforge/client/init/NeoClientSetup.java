package xiao.battleroyale.compat.neoforge.client.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import xiao.battleroyale.api.client.init.IClientSetup;
import xiao.battleroyale.client.init.ClientSetup;

public class NeoClientSetup {

    private static final IClientSetup CLIENT_SETUP = ClientSetup.get();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CLIENT_SETUP::onClientSetup);
    }
}