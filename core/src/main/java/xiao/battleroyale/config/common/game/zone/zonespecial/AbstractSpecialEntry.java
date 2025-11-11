package xiao.battleroyale.config.common.game.zone.zonespecial;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.special.IZoneSpecialEntry;
import xiao.battleroyale.api.game.zone.special.ZoneSpecialTag;

public abstract class AbstractSpecialEntry implements IZoneSpecialEntry {

    public AbstractSpecialEntry() {
        ;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneSpecialTag.TYPE_NAME, getType());
        return jsonObject;
    }
}
