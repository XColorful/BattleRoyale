package xiao.battleroyale.api.event.game.zone;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.event.EventDispatcher;

public class ZoneCreatedEvent extends AbstractZoneEvent {

    protected final boolean success;

    public ZoneCreatedEvent(IGameManager gameManager, IGameZone gameZone, boolean success) {
        super(gameManager, gameZone);
        this.success = success;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_CREATED_EVENT;
    }

    public boolean isSuccess() {
        return this.success;
    }

    @Override public String getTextName() {
        return String.format("CBR Zone %s Created Event", gameZone.getZoneId());
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(ZoneCreatedEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
