package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerLoggedInEvent;

public class PlayerLoggedInLow extends AbstractEventCommon {

    private static class PlayerLoggedInLowHolder {
        private static final PlayerLoggedInLow INSTANCE = new PlayerLoggedInLow();
    }

    public static PlayerLoggedInLow get() {
        return PlayerLoggedInLow.PlayerLoggedInLowHolder.INSTANCE;
    }

    private PlayerLoggedInLow() {
        super(EventType.PLAYER_LOGGED_IN_EVENT);
    }

    @Override
    protected void registerToForge() {
        MinecraftForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToForge() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected ForgeEvent getForgeEventType(Event event) {
        return new ForgePlayerLoggedInEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        super.onEvent(event);
    }
}
