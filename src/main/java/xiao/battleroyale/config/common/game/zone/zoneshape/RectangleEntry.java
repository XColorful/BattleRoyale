package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.RectangleShape;

public class RectangleEntry implements IZoneShapeEntry {

    private StartEntry startEntry;
    private EndEntry endEntry;

    public RectangleEntry(StartEntry startEntry, EndEntry endEntry) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
    }

    @Override
    public String getType() {
        return ZoneShapeTag.RECTANGLE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.RECTANGLE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new RectangleShape(startEntry, endEntry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneShapeTag.TYPE_NAME, getType());
        jsonObject.add(ZoneShapeTag.START, startEntry.toJson());
        jsonObject.add(ZoneShapeTag.END, endEntry.toJson());
        return jsonObject;
    }

    public static RectangleEntry fromJson(JsonObject jsonObject) {
        JsonObject startEntryObject = jsonObject.has(ZoneShapeTag.START) ? jsonObject.getAsJsonObject(ZoneShapeTag.START) : null;
        JsonObject endEntryObject = jsonObject.has(ZoneShapeTag.END) ? jsonObject.getAsJsonObject(ZoneShapeTag.END) : null;
        if (startEntryObject == null || endEntryObject == null) {
            return null;
        }
        StartEntry startEntry = StartEntry.fromJson(startEntryObject);
        EndEntry endEntry = EndEntry.fromJson(endEntryObject);
        if (startEntry == null || endEntry == null) {
            return null;
        }
        return new RectangleEntry(startEntry, endEntry);
    }
}