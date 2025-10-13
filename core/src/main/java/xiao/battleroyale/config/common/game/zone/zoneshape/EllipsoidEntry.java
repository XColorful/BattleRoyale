package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.EllipsoidShape;

public class EllipsoidEntry extends AbstractSimpleEntry {

    public EllipsoidEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }
    @Override public @NotNull EllipsoidEntry copy() {
        return new EllipsoidEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.ELLIPSOID;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.ELLIPSOID;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new EllipsoidShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static EllipsoidEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for EllipsoidEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for EllipsoidEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new EllipsoidEntry(startEntry, endEntry, badShape);
    }
}
