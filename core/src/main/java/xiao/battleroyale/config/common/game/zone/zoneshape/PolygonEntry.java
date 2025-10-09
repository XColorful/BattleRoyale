package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.PolygonShape;
import xiao.battleroyale.util.JsonUtils;

public class PolygonEntry extends AbstractSimpleEntry {

    public int segments; // 边数

    public PolygonEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape, int segments) {
        super(startEntry, endEntry, badShape);
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
        return new PolygonShape(startEntry, endEntry, badShape, segments);
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

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        int segments = JsonUtils.getJsonInt(jsonObject, ZoneShapeTag.SEGMENTS, 3);
        if (segments < 3) {
            return null;
        }

        return new PolygonEntry(startEntry, endEntry, badShape, segments);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(ZoneShapeTag.SEGMENTS, segments);
        return jsonObject;
    }
}