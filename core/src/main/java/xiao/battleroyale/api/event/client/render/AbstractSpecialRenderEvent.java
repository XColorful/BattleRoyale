package xiao.battleroyale.api.event.client.render;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.event.client.AbstractClientEvent;

public abstract class AbstractSpecialRenderEvent extends AbstractClientEvent {

    protected final String protocol;
    protected final @NotNull JsonObject jsonTag;

    public AbstractSpecialRenderEvent(IClientGameDataManager clientGameDataManager, String protocol, @NotNull JsonObject jsonTag) {
        super(clientGameDataManager);
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    public String getProtocol() {
        return protocol;
    }

    public @NotNull JsonObject getJsonTag() {
        return jsonTag;
    }
}
