package xiao.battleroyale.compat.forge.init;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.init.CompatInit;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeCompatInit {

    private static final CompatInit COMPAT_INIT = CompatInit.get();

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        COMPAT_INIT.onLoadComplete();
    }
}
