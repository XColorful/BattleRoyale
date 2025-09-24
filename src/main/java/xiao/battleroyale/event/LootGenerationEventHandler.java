package xiao.battleroyale.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.loot.CommonLootManager;

public class LootGenerationEventHandler {

    private LootGenerationEventHandler() {}

    private static class LootGenerationEventHandlerHolder {
        private static final LootGenerationEventHandler INSTANCE = new LootGenerationEventHandler();
    }

    public static LootGenerationEventHandler get() {
        return LootGenerationEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        LootGenerationEventHandler handler = get();
        MinecraftForge.EVENT_BUS.register(handler);
        BattleRoyale.LOGGER.info("LootGenerationEventHandler registered for funcTick events.");
    }

    public static void unregister() {
        LootGenerationEventHandler handler = get();
        MinecraftForge.EVENT_BUS.unregister(handler);
        BattleRoyale.LOGGER.info("LootGenerationEventHandler unregistered from funcTick events.");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            boolean taskCompletedOrInterrupted = CommonLootManager.get().onTick(event);
            if (taskCompletedOrInterrupted) {
                unregister();
            }
        }
    }
}