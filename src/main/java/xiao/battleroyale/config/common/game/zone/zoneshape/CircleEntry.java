package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.CircleShape;

public class CircleEntry extends AbstractSimpleEntry {

    public CircleEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.CIRCLE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.CIRCLE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new CircleShape(startEntry, endEntry, badShape);
    }

    @Nullable
    public static CircleEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for CircleEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for CircleEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new CircleEntry(startEntry, endEntry, badShape);
    }
}