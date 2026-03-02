package xiao.battleroyale.compat.neoforge.client.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientModEvent;
import xiao.battleroyale.client.init.ClientModEvent;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoClientModEvent {

    public static IClientModEvent CLIENT_MOD_EVENT = ClientModEvent.get();

    @SubscribeEvent
    public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CLIENT_MOD_EVENT.onClientPlayerLoggingOut(event.getPlayer());
    }
}
