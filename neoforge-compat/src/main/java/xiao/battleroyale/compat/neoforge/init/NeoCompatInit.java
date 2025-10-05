package xiao.battleroyale.compat.neoforge.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import xiao.battleroyale.init.CompatInit;

public class NeoCompatInit {

    private static final CompatInit COMPAT_INIT = CompatInit.get();

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        COMPAT_INIT.onLoadComplete();
    }
}