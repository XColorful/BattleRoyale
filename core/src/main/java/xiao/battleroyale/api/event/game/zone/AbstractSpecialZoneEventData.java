package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.zone.ZoneManager;

public abstract class AbstractSpecialZoneEventData extends AbstractGameEventData {

    public @NotNull final ZoneManager.ZoneTickContext zoneTickContext;
    public final String protocol;
    public @NotNull final CompoundTag tag;

    public AbstractSpecialZoneEventData(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                                    String protocol, @NotNull CompoundTag tag) {
        super(gameManager);
        this.zoneTickContext = zoneTickContext;
        this.protocol = protocol;
        this.tag = tag;
    }
}
