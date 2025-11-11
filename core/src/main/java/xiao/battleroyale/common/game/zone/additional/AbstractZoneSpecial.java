package xiao.battleroyale.common.game.zone.additional;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IAdditionalZone;

public abstract class AbstractZoneSpecial implements IAdditionalZone {

    protected @NotNull JsonObject jsonTag;

    public AbstractZoneSpecial(@Nullable JsonObject jsonTag) {
        this.jsonTag = jsonTag != null ? jsonTag : new JsonObject();
    }
}
