package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

public abstract class AbstractSimpleEntry implements IZoneShapeEntry {

    protected final StartEntry startEntry;
    protected final EndEntry endEntry;

    public AbstractSimpleEntry(StartEntry startEntry, EndEntry endEntry) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneShapeTag.TYPE_NAME, getType());
        jsonObject.add(ZoneShapeTag.START, startEntry.toJson());
        jsonObject.add(ZoneShapeTag.END, endEntry.toJson());
        return jsonObject;
    }
}
