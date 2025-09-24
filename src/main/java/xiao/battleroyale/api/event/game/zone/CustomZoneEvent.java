package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;

public class CustomZoneEvent extends AbstractGameEvent {

    protected @NotNull final ZoneTickContext zoneTickContext;
    protected final String protocol;
    protected @NotNull final CompoundTag nbt;
    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;

    public CustomZoneEvent(IGameManager gameManager, @NotNull ZoneTickContext zoneTickContext,
                           String protocol, @NotNull CompoundTag nbt, @NotNull GamePlayer gamePlayer, @Nullable LivingEntity livingEntity) {
        super(gameManager);
        this.zoneTickContext = zoneTickContext;
        this.protocol = protocol;
        this.nbt = nbt;
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
    }

    public @NotNull ZoneTickContext getZoneTickContext() {
        return this.zoneTickContext;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public @NotNull CompoundTag getNbt() {
        return this.nbt;
    }

    public @NotNull GamePlayer gamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
}
