package xiao.battleroyale.api.event.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager;

public class CustomZoneEventData extends AbstractSpecialZoneEventData {

    public @NotNull final GamePlayer gamePlayer;
    public @Nullable final LivingEntity livingEntity;

    public CustomZoneEventData(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext, String protocol,
                               @NotNull CompoundTag nbt, @NotNull GamePlayer gamePlayer, @Nullable LivingEntity livingEntity) {
        super(gameManager, zoneTickContext, protocol, nbt);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.CUSTOM_ZONE_EVENT;
    }
}
