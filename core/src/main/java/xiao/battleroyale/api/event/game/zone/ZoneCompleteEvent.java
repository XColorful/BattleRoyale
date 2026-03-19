package xiao.battleroyale.api.event.game.zone;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public class ZoneCompleteEvent extends AbstractZoneEvent {

    public ZoneCompleteEvent(IGameManager gameManager, IGameZone gameZone) {
        super(gameManager, gameZone);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_COMPLETE_EVENT;
    }

    @Override public String getTextName() {
        return String.format("CBR Zone %s Complete Event", gameZone.getZoneId());
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
