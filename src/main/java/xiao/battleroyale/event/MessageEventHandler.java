package xiao.battleroyale.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.message.MessageManager;

public class MessageEventHandler {

    private MessageEventHandler() {}

    private static class MessageEventHandlerHolder {
        private static final MessageEventHandler INSTANCE = new MessageEventHandler();
    }

    public static MessageEventHandler get() {
        return MessageEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.info("MessageEventHandler registered");
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        BattleRoyale.LOGGER.info("MessageEventHandler unregistered");
    }

    @SubscribeEvent
    public void onMinecraftTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MessageManager.get().tick();
    }
}
