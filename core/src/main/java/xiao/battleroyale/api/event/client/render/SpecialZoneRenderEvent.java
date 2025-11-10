package xiao.battleroyale.api.event.client.render;

import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.client.AbstractClientEvent;

public class SpecialZoneRenderEvent extends AbstractClientEvent {

    protected final Object clientZoneRenderer;

    public SpecialZoneRenderEvent(IClientGameDataManager clientGameDataManager, Object clientZoneRenderer) {
        super(clientGameDataManager);
        this.clientZoneRenderer = clientZoneRenderer;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.SPECIAL_ZONE_RENDER_EVENT;
    }

    /**
     * 需手动转成IClientZoneRenderer
     */
    public Object getClientZoneRenderer() {
        return clientZoneRenderer;
    }
}
