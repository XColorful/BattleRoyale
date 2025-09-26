package xiao.battleroyale.init;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.loot.GameLootManager;

@Mod.EventBusSubscriber(modid = BattleRoyale.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        BattleRoyale.setMinecraftServer(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        GameManager.get().onServerStopping();
        GameLootManager.get().awaitTerminationOnShutdown();
        BattleRoyale.setMinecraftServer(null);
    }
}
