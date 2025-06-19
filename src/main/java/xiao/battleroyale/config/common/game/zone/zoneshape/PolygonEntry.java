package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.PolygonShape;

public class PolygonEntry extends AbstractSimpleEntry {

    private final int segments; // 边数

    public PolygonEntry(StartEntry startEntry, EndEntry endEntry, int segments) {
        super(startEntry, endEntry);
        this.segments = segments;
    }

    @Override
    public String getType() {
        return ZoneShapeTag.POLYGON;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.POLYGON;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new PolygonShape(startEntry, endEntry, segments);
    }

    @Nullable
    public static PolygonEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for PolygonEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for PolygonEntry, skipped");
            return null;
        }

        int segments = jsonObject.has(ZoneShapeTag.SEGMENTS) ? jsonObject.get(ZoneShapeTag.SEGMENTS).getAsInt() : 3;
        if (segments < 3) {
            return null;
        }

        return new PolygonEntry(startEntry, endEntry, segments);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneShapeTag.TYPE_NAME, getType());
        jsonObject.add(ZoneShapeTag.START, startEntry.toJson());
        jsonObject.add(ZoneShapeTag.END, endEntry.toJson());
        jsonObject.addProperty(ZoneShapeTag.SEGMENTS, segments);
        return jsonObject;
    }
}