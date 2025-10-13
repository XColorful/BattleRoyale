package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.EllipseShape;

import javax.annotation.Nullable;

public class EllipseEntry extends AbstractSimpleEntry {

    public EllipseEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }
    @Override public @NotNull EllipseEntry copy() {
        return new EllipseEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.ELLIPSE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.ELLIPSE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new EllipseShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static EllipseEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for HexagonEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for HexagonEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new EllipseEntry(startEntry, endEntry, badShape);
    }
}
