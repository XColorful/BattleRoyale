package xiao.battleroyale.compat.fabric.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import xiao.battleroyale.api.init.IModEvent;
import xiao.battleroyale.init.ModEvent;

public class FabricModEvent {

    public static final IModEvent MOD_EVENT = ModEvent.get();

    public static void init() {
        // 对应 Forge 的 ServerStartingEvent
        ServerLifecycleEvents.SERVER_STARTING.register(MOD_EVENT::onServerStarting);

        // 对应 Forge 的 ServerStoppingEvent
        ServerLifecycleEvents.SERVER_STOPPING.register(MOD_EVENT::onServerStopping);
    }
}