package xiao.battleroyale.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.loot.LootGenerationManager;

public class LootGenerationEventHandler {

    private static LootGenerationEventHandler instance;

    private LootGenerationEventHandler() {
    }

    public static LootGenerationEventHandler getInstance() {
        if (instance == null) {
            instance = new LootGenerationEventHandler();
        }
        return instance;
    }

    public static void register() {
        LootGenerationEventHandler handler = getInstance();
        MinecraftForge.EVENT_BUS.register(handler);
        BattleRoyale.LOGGER.info("LootGenerationEventHandler registered for tick events.");
    }

    public static void unregister() {
        LootGenerationEventHandler handler = getInstance();
        MinecraftForge.EVENT_BUS.unregister(handler);
        instance = null;
        BattleRoyale.LOGGER.info("LootGenerationEventHandler unregistered from tick events.");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            boolean taskCompletedOrInterrupted = LootGenerationManager.get().onTick(event);
            if (taskCompletedOrInterrupted) {
                unregister();
            }
        }
    }
}