package xiao.battleroyale.event.client;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.event.AbstractEventHandler;

public class ClientRenderEventHandler extends AbstractEventHandler {

    private static class ClientRenderEventHandlerHolder {
        private static final ClientRenderEventHandler INSTANCE = new ClientRenderEventHandler();
    }

    public static ClientRenderEventHandler get() {
        return ClientRenderEventHandlerHolder.INSTANCE;
    }

    private ClientRenderEventHandler() {
        super(CustomEventType.SPECIAL_ZONE_RENDER_EVENT);
    }
}
