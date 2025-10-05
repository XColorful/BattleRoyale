package xiao.battleroyale.compat.neoforge.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import xiao.battleroyale.api.init.ICommonSetup;
import xiao.battleroyale.init.CommonSetup;

public class NeoCommonSetup {

    private static final ICommonSetup COMMON_SETUP = CommonSetup.get();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(COMMON_SETUP::onCommonSetup);
    }
}