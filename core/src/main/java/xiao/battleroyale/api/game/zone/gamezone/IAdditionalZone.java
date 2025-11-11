package xiao.battleroyale.api.game.zone.gamezone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.special.IZoneSpecialClient;
import xiao.battleroyale.common.game.zone.additional.ZoneSpecialHandler;
import xiao.battleroyale.config.common.game.zone.zonespecial.ZoneSpecialType;

public interface IAdditionalZone extends IZoneSpecialClient {

    default ZoneSpecialType getSpecialType() {
        return getSpecialHandlerType().getSpecialType();
    }

    ZoneSpecialHandler getSpecialHandlerType();

    @NotNull CompoundTag addMessageTag(IGameZone gameZone);
}
