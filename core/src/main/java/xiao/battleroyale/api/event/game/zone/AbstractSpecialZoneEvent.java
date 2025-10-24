package xiao.battleroyale.api.event.game.zone;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.zone.ZoneManager;

public abstract class AbstractSpecialZoneEvent extends AbstractGameEvent {

    protected @NotNull
    final ZoneManager.ZoneTickContext zoneTickContext;
    protected final String protocol;
    protected @NotNull final JsonObject jsonTag;

    public AbstractSpecialZoneEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                                    String protocol, @NotNull JsonObject jsonTag) {
        super(gameManager);
        this.zoneTickContext = zoneTickContext;
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    public @NotNull ZoneManager.ZoneTickContext getZoneTickContext() {
        return this.zoneTickContext;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public @NotNull JsonObject getJsonTag() {
        return this.jsonTag;
    }

    @Deprecated
    public @NotNull JsonObject getTag() {
        return getJsonTag();
    }
}
