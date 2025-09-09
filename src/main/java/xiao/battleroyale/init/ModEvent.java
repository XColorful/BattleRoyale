package xiao.battleroyale.init;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        BattleRoyale.setMinecraftServer(event.getServer());
    }
}
