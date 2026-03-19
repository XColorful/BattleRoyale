package xiao.battleroyale.api.event.game.zone;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

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
}
