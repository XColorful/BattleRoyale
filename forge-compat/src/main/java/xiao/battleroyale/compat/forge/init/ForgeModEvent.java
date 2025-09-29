package xiao.battleroyale.compat.forge.init;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.init.ModEvent;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeModEvent {

    private static final ModEvent MOD_EVENT = ModEvent.get();

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MOD_EVENT.onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MOD_EVENT.onServerStopping(event.getServer());
    }
}