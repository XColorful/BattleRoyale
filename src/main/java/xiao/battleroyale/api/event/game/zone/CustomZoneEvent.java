package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;

public class CustomZoneEvent extends AbstractSpecialZoneEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;

    public CustomZoneEvent(IGameManager gameManager, @NotNull ZoneTickContext zoneTickContext, String protocol,
                           @NotNull CompoundTag nbt, @NotNull GamePlayer gamePlayer, @Nullable LivingEntity livingEntity) {
        super(gameManager, zoneTickContext, protocol, nbt);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
    }

    public @NotNull GamePlayer gamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
}
