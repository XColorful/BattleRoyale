package xiao.battleroyale.compat.forge.client.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.init.IClientModEvent;
import xiao.battleroyale.client.init.ClientModEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ForgeClientModEvent {

    public static IClientModEvent CLIENT_MOD_EVENT = ClientModEvent.get();

    @SubscribeEvent
    public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CLIENT_MOD_EVENT.onClientPlayerLoggingOut(event.getPlayer());
    }
}
