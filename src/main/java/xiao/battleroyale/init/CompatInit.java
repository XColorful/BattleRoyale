package xiao.battleroyale.init;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompatInit {

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        PlayerRevive.get().checkLoaded();
    }

}
