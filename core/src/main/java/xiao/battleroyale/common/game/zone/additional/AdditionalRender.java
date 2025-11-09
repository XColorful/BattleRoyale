package xiao.battleroyale.common.game.zone.additional;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.config.common.game.zone.zonespecial.ZoneSpecialType;

public class AdditionalRender extends AbstractZoneSpecial {

    protected String protocol;
    protected @NotNull JsonObject jsonTag;

    public AdditionalRender(String protocol, @NotNull JsonObject jsonTag) {
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    @Override
    public ZoneSpecialType getSpecialType() {
        return ZoneSpecialType.RENDER;
    }
}
