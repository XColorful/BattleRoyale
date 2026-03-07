package xiao.battleroyale.compat.neoforge.client.init;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientModEvent;
import xiao.battleroyale.client.init.ClientModEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class NeoClientModEvent {

    public static IClientModEvent CLIENT_MOD_EVENT = ClientModEvent.get();

    @SubscribeEvent
    public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CLIENT_MOD_EVENT.onClientPlayerLoggingOut(event.getPlayer());
    }
}
