package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;

public class AbstractSpecialZoneEvent extends AbstractGameEvent {

    protected @NotNull final ZoneTickContext zoneTickContext;
    protected final String protocol;
    protected @NotNull final CompoundTag tag;

    public AbstractSpecialZoneEvent(IGameManager gameManager, @NotNull ZoneTickContext zoneTickContext,
                                    String protocol, @NotNull CompoundTag tag) {
        super(gameManager);
        this.zoneTickContext = zoneTickContext;
        this.protocol = protocol;
        this.tag = tag;
    }

    public @NotNull ZoneTickContext getZoneTickContext() {
        return this.zoneTickContext;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public @NotNull CompoundTag getTag() {
        return this.tag;
    }

    public @NotNull CompoundTag getNbt() {
        return this.tag;
    }
}
