package xiao.battleroyale.init;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.network.GameInfoHandler;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {

    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(GameInfoHandler::init);
        event.enqueueWork(BattleRoyale.getModConfigManager()::reloadAllConfigs);
    }

}
